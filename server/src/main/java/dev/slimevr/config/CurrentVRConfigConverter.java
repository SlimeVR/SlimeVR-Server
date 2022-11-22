package dev.slimevr.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.jonpeterson.jackson.module.versioning.VersionedModelConverter;

import java.util.Map;


public class CurrentVRConfigConverter implements VersionedModelConverter {

	@Override
	public ObjectNode convert(
		ObjectNode modelData,
		String modelVersion,
		String targetModelVersion,
		JsonNodeFactory nodeFactory
	) {

		int version = Integer.parseInt(modelVersion);

		// Configs less than version 2 need a migration to the latest config
		if (version < 2) {
			// Move zoom to the window config
			ObjectNode windowNode = (ObjectNode) modelData.get("window");
			DoubleNode zoomNode = (DoubleNode) modelData.get("zoom");
			if (windowNode != null && zoomNode != null) {
				windowNode.set("zoom", zoomNode);
				modelData.remove("zoom");
			}

			// Change trackers list to map
			ArrayNode oldTrackersNode = modelData.withArray("trackers");
			if (oldTrackersNode != null) {
				var trackersIter = oldTrackersNode.iterator();
				ObjectNode trackersNode = nodeFactory.objectNode();
				while (trackersIter.hasNext()) {
					JsonNode node = trackersIter.next();
					JsonNode resultNode = TrackerConfig.toV2(node, nodeFactory);
					trackersNode.set(node.get("name").asText(), resultNode);
				}
				modelData.set("trackers", trackersNode);
			}

			// Rename bridge to bridges
			ObjectNode bridgeNode = (ObjectNode) modelData.get("bridge");
			if (bridgeNode != null) {
				modelData.set("bridges", bridgeNode);
				modelData.remove("bridge");
			}

			// Move body to skeleton (and merge it to current skeleton)
			ObjectNode bodyNode = (ObjectNode) modelData.get("body");
			if (bodyNode != null) {
				var bodyIter = bodyNode.fields();
				ObjectNode skeletonNode = (ObjectNode) modelData.get("skeleton");
				if (skeletonNode == null) {
					skeletonNode = nodeFactory.objectNode();
				}

				ObjectNode offsetsNode = nodeFactory.objectNode();
				while (bodyIter.hasNext()) {
					Map.Entry<String, JsonNode> node = bodyIter.next();
					// Filter only number values because other types would be
					// stuff
					// that didn't get migrated correctly before
					if (node.getValue().isNumber()) {
						offsetsNode.set(node.getKey(), node.getValue());
					}
				}

				// Fix calibration wolf typos
				offsetsNode.set("shouldersWidth", bodyNode.get("shoulersWidth"));
				offsetsNode.set("shouldersDistance", bodyNode.get("shoulersDistance"));
				offsetsNode.remove("shoulersWidth");
				offsetsNode.remove("shoulersDistance");
				skeletonNode.set("offsets", offsetsNode);
				modelData.set("skeleton", skeletonNode);
				modelData.remove("body");
			}
		}

		return modelData;
	}
}
