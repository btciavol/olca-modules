package org.openlca.jsonld;

import org.openlca.core.model.Actor;
import org.openlca.core.model.Category;
import org.openlca.core.model.Flow;
import org.openlca.core.model.FlowProperty;
import org.openlca.core.model.Location;
import org.openlca.core.model.Process;
import org.openlca.core.model.RootEntity;
import org.openlca.core.model.UnitGroup;

import com.google.gson.JsonObject;

/**
 * Utility class for handling references to other entities.
 */
class Refs {

	private Refs() {
	}

	public static <T extends RootEntity> JsonObject put(T entity,
			EntityStore store) {
		if (entity == null)
			return null;
		JsonObject ref = createRef(entity);
		Writer<T> writer = writer(entity, store);
		if (writer != null)
			writer.write(entity);
		return ref;
	}

	@SuppressWarnings("unchecked")
	private static <T extends RootEntity> Writer<T> writer(T entity,
			EntityStore store) {
		if (entity == null || store == null)
			return null;
		if (entity instanceof Actor)
			return Writer.class.cast(new ActorWriter(store));
		if (entity instanceof Category)
			return Writer.class.cast(new CategoryWriter(store));
		if (entity instanceof FlowProperty)
			return Writer.class.cast(new FlowPropertyWriter(store));
		if (entity instanceof Flow)
			return Writer.class.cast(new FlowWriter(store));
		if (entity instanceof Location)
			return Writer.class.cast(new LocationWriter(store));
		if (entity instanceof Process)
			return Writer.class.cast(new ProcessWriter(store));
		if (entity instanceof UnitGroup)
			return Writer.class.cast(new UnitGroupWriter(store));
		else
			return null;
	}

	static JsonObject createRef(RootEntity entity) {
		if (entity == null)
			return null;
		JsonObject ref = new JsonObject();
		String type = entity.getClass().getSimpleName();
		ref.addProperty("@type", type);
		ref.addProperty("@id", entity.getRefId());
		ref.addProperty("name", entity.getName());
		return ref;
	}

}
