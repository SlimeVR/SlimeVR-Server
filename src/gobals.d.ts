declare module 'ip-bigint' {
  function stringifyIp(options: {
    number: bigint;
    version: 4 | 6;
    ipv4mapped?: boolean;
  }): string;
}
