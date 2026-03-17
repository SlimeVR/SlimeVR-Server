import { sentryVitePlugin } from '@sentry/vite-plugin';
import react from '@vitejs/plugin-react';
import { defineConfig, PluginOption } from 'vite';
import { execSync } from 'child_process';
import path from 'path';
import { visualizer } from 'rollup-plugin-visualizer';
import jotaiReactRefresh from 'jotai/babel/plugin-react-refresh';

const commitHash = execSync('git rev-parse --verify --short HEAD').toString().trim();
const versionTag = execSync('git --no-pager tag --sort -taggerdate --points-at HEAD')
  .toString()
  .split('\n')[0]
  .trim();
// If not empty then it's not clean
const gitCleanString = execSync('git status --porcelain').toString();
const gitClean = gitCleanString ? false : true;
if (!gitClean) console.log('Git is dirty because of:\n' + gitCleanString);

console.log(`version is ${versionTag || commitHash}${gitClean ? '' : '-dirty'}`);

// Detect fluent file changes
export function i18nHotReload(): PluginOption {
  return {
    name: 'i18n-hot-reload',
    handleHotUpdate({ file, server }) {
      if (file.endsWith('.ftl')) {
        console.log('Fluent files updated');
        server.hot.send({
          type: 'custom',
          event: 'locales-update',
        });
      }
    },
  };
}

export function videoCalibrationProxy(): PluginOption {
  function buildWebcamOfferUrl(host: string, port: number) {
    const normalizedHost =
      host.includes(':') && !host.startsWith('[') ? `[${host}]` : host;

    return `http://${normalizedHost}:${port}/offer`;
  }

  async function handleRequest(req: NodeJS.ReadableStream) {
    const chunks: Buffer[] = [];

    for await (const chunk of req) {
      chunks.push(Buffer.isBuffer(chunk) ? chunk : Buffer.from(chunk));
    }

    const body = JSON.parse(Buffer.concat(chunks).toString('utf8')) as {
      host?: unknown;
      port?: unknown;
      sdp?: unknown;
    };

    if (
      typeof body.host !== 'string' ||
      typeof body.port !== 'number' ||
      typeof body.sdp !== 'string'
    ) {
      throw new Error('Invalid webcam offer payload');
    }

    const response = await fetch(buildWebcamOfferUrl(body.host, body.port), {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        sdp: body.sdp,
      }),
    });

    if (!response.ok) {
      throw new Error(`Offer request failed with status ${response.status}`);
    }

    const responseBody = (await response.json()) as { sdp?: unknown };
    if (typeof responseBody.sdp !== 'string' || responseBody.sdp.length === 0) {
      throw new Error('Webcam response did not contain an SDP answer');
    }

    return JSON.stringify({
      sdp: responseBody.sdp,
    });
  }

  return {
    name: 'video-calibration-proxy',
    configureServer(server) {
      server.middlewares.use('/video-calibration-api/offer', async (req, res) => {
        if (req.method !== 'POST') {
          res.statusCode = 405;
          res.setHeader('Content-Type', 'application/json');
          res.end(JSON.stringify({ error: 'Method not allowed' }));
          return;
        }

        try {
          const responseBody = await handleRequest(req);
          res.statusCode = 200;
          res.setHeader('Content-Type', 'application/json');
          res.end(responseBody);
        } catch (error) {
          res.statusCode = 502;
          res.setHeader('Content-Type', 'application/json');
          res.end(
            JSON.stringify({
              error:
                error instanceof Error ? error.message : 'Unknown webcam proxy error',
            })
          );
        }
      });
    },
  };
}

// https://vitejs.dev/config/
export default defineConfig({
  define: {
    __COMMIT_HASH__: JSON.stringify(commitHash),
    __VERSION_TAG__: JSON.stringify(versionTag),
    __GIT_CLEAN__: gitClean,
  },
  plugins: [
    react({ babel: { plugins: [jotaiReactRefresh] } }),
    i18nHotReload(),
    videoCalibrationProxy(),
    visualizer() as PluginOption,
    sentryVitePlugin({
      org: 'slimevr',
      project: 'slimevr-server-gui-react',
    }),
  ],
  build: {
    target: 'es2022',
    emptyOutDir: true,

    commonjsOptions: {
      include: [/solarxr-protocol/, /node_modules/],
    },

    sourcemap: true,
  },
  optimizeDeps: {
    esbuildOptions: {
      target: 'es2022',
    },
    needsInterop: ['solarxr-protocol'],
    include: ['solarxr-protocol'],
  },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src'),
    },
  },
  css: {
    preprocessorOptions: {
      scss: {
        api: 'modern',
      },
    },
  },
});
