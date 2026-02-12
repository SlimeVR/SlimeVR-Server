import { existsSync, mkdirSync, readFileSync, writeFileSync } from "fs";
import { dirname, join } from "path";
import { logger } from "./logger";
import { getGuiDataFolder } from "./paths";


export class CustomStore {
  private data: Record<string, unknown> = {};
  private saveTimeout: NodeJS.Timeout | null = null;
  private filePath: string;
  private debounceMs: number;

  constructor(filePath: string, debounceMs: number = 2000) {
    this.filePath = filePath;
    this.debounceMs = debounceMs;
    this.load();
  }

  /** Load data from disk into memory */
  private load() {
    try {
      if (existsSync(this.filePath)) {
        const raw = readFileSync(this.filePath, 'utf-8');
        this.data = JSON.parse(raw);
      }
    } catch (err) {
      logger.error(err, `Failed to load store at ${this.filePath}`);
      this.data = {};
    }
  }

  /** Set a key and trigger the debounced auto-save */
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

  save(): boolean {
    try {
      if (this.saveTimeout) clearTimeout(this.saveTimeout);

      const dir = dirname(this.filePath);
      if (!existsSync(dir)) mkdirSync(dir, { recursive: true });

      writeFileSync(this.filePath, JSON.stringify(this.data, null, 2), 'utf-8');
      return true;
    } catch (err) {
      logger.error(err, 'Save failed', this.filePath);
      return false;
    }
  }
}

export const stores = {
  settings: new CustomStore(join(getGuiDataFolder(), 'gui-settings.dat'), 1000),
  cache: new CustomStore(join(getGuiDataFolder(), 'gui-cache.dat'), 100),
};
