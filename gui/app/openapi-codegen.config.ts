import {
  generateSchemaTypes,
  generateReactQueryComponents,
} from '@openapi-codegen/typescript';
import { defineConfig } from '@openapi-codegen/cli';
import dotenv from 'dotenv';

dotenv.config()

export default defineConfig({
  firmwareTool: {
    from: {
      source: 'url',
      url: process.env.FIRMWARE_TOOL_SCHEMA_URL ?? 'http://localhost:3000/api-json',
    },
    outputDir: 'src/firmware-tool-api',
    to: async (context) => {
      const filenamePrefix = 'firmwareTool';
      const { schemasFiles } = await generateSchemaTypes(context, {
        filenamePrefix,
      });
      await generateReactQueryComponents(context, {
        filenamePrefix,
        schemasFiles,
      });
    },
  },
});
