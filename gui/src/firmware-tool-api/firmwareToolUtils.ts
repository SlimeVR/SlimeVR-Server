type ComputeRange<
  N extends number,
  Result extends Array<unknown> = []
> = Result['length'] extends N
  ? Result
  : ComputeRange<N, [...Result, Result['length']]>;

export type ClientErrorStatus = Exclude<
  ComputeRange<500>[number],
  ComputeRange<400>[number]
>;
export type ServerErrorStatus = Exclude<
  ComputeRange<600>[number],
  ComputeRange<500>[number]
>;
