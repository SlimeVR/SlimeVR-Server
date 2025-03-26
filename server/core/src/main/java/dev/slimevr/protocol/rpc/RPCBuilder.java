package dev.slimevr.protocol.rpc;

import com.google.flatbuffers.FlatBufferBuilder;
import dev.slimevr.tracking.processor.HumanPoseManager;
import dev.slimevr.tracking.processor.config.SkeletonConfigOffsets;
import solarxr_protocol.rpc.SkeletonConfigResponse;
import solarxr_protocol.rpc.SkeletonPart;


public class RPCBuilder {

	public static int createSkeletonConfig(
		FlatBufferBuilder fbb,
		HumanPoseManager humanPoseManager
	) {
		int[] partsOffsets = new int[SkeletonConfigOffsets.values().length];

		for (int index = 0; index < SkeletonConfigOffsets.values().length; index++) {
			SkeletonConfigOffsets val = SkeletonConfigOffsets.values[index];
			int part = SkeletonPart
				.createSkeletonPart(fbb, val.id, humanPoseManager.getOffset(val));
			partsOffsets[index] = part;
		}

		int parts = SkeletonConfigResponse.createSkeletonPartsVector(fbb, partsOffsets);
		return SkeletonConfigResponse.createSkeletonConfigResponse(fbb, parts);
	}
}
