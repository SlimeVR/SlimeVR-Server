// implemetation of https://en.wikipedia.org/wiki/Fowler%E2%80%93Noll%E2%80%93Vo_hash_function
export function normalizedHash(str: string) {
  let hash = 2166136261;
  for (let i = 0; i < str.length; i++) {
    hash ^= str.charCodeAt(i);
    hash = Math.imul(hash, 16777619); // FNV prime
  }

  // Convert to unsigned 32-bit integer and normalize (0, 1)
  return (hash >>> 0) / 2 ** 32;
}
