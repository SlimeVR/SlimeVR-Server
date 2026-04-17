import { access, mkdir, readFile, writeFile } from "fs/promises";
import { dirname, join } from "path";
import { logger } from "./logger";
import { getGuiDataFolder } from "./paths";


export class CustomStore {
  private data: Record<string, unknown> = {};
  private saveTimeout: NodeJS.Timeout | null = null;
  private filePath: string;
  private debounceMs: number;

  private constructor(filePath: string, debounceMs: number = 2000) {
    this.filePath = filePath;
    this.debounceMs = debounceMs;
  }

  static async create(filePath: string, debounceMs: number = 2000): Promise<CustomStore> {
    const store = new CustomStore(filePath, debounceMs);
    await store.load();
    return store;
  }

  private async load(): Promise<void> {
    try {
      await access(this.filePath);
      const raw = await readFile(this.filePath, 'utf-8');
      this.data = JSON.parse(raw);
    } catch {
      this.data = {};
      logger.warn(`No existing store found at ${this.filePath}, starting with empty store.`);
    }
  }

  set(key: string, value: unknown) {
    this.data[key] = value;
    this.triggerAutoSave();
  }

  get<T>(key: string): T | undefined {
    return this.data[key] as T;
  }

  delete(key: string): boolean {
    if (key in this.data) {
      delete this.data[key];
      this.triggerAutoSave();
      return true;
    }
    return false;
  }

  private triggerAutoSave() {
    if (this.saveTimeout) clearTimeout(this.saveTimeout);
    this.saveTimeout = setTimeout(() => {
      this.save();
    }, this.debounceMs);
  }

  async save(): Promise<boolean> {
    try {
      if (this.saveTimeout) clearTimeout(this.saveTimeout);

      await mkdir(dirname(this.filePath), { recursive: true });
      await writeFile(this.filePath, JSON.stringify(this.data, null, 2), 'utf-8');
      return true;
    } catch (err) {
      logger.error(err, 'Save failed', this.filePath);
      return false;
    }
  }
}

export async function initStores() {
  return {
    settings: await CustomStore.create(join(getGuiDataFolder(), 'gui-settings.dat'), 1000),
    cache: await CustomStore.create(join(getGuiDataFolder(), 'gui-cache.dat'), 100),
  };
}
