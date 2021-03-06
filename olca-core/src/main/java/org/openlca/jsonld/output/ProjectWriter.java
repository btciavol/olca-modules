package org.openlca.jsonld.output;

import org.openlca.core.model.FlowProperty;
import org.openlca.core.model.ModelType;
import org.openlca.core.model.Project;
import org.openlca.core.model.ProjectVariant;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.openlca.jsonld.Json;

class ProjectWriter extends Writer<Project> {

	ProjectWriter(ExportConfig conf) {
		super(conf);
	}

	@Override
	JsonObject write(Project p) {
		JsonObject obj = super.write(p);
		if (obj == null)
			return null;
		Json.put(obj, "creationDate", p.creationDate);
		Out.put(obj, "functionalUnit", p.functionalUnit);
		Out.put(obj, "goal", p.goal);
		Json.put(obj, "lastModificationDate", p.lastModificationDate);
		Out.put(obj, "author", p.author, conf);
		Out.put(obj, "impactMethod", createRef(ModelType.IMPACT_METHOD, p.impactMethodId));
		Out.put(obj, "nwSet", createRef(ModelType.NW_SET, p.nwSetId));
		mapVariants(obj, p);
		GlobalParameters.sync(p, conf);
		return obj;
	}

	private void mapVariants(JsonObject json, Project p) {
		JsonArray array = new JsonArray();
		for (ProjectVariant v : p.variants) {
			JsonObject obj = new JsonObject();
			array.add(obj);
			Out.put(obj, "@type", ProjectVariant.class.getSimpleName());
			Out.put(obj, "name", v.name);
			Out.put(obj, "productSystem", v.productSystem, conf, Out.REQUIRED_FIELD);
			Out.put(obj, "amount", v.amount);
			Out.put(obj, "unit", v.unit, conf, Out.REQUIRED_FIELD);
			Out.put(obj, "allocationMethod", v.allocationMethod);
			FlowProperty prop = null;
			if (v.flowPropertyFactor != null) {
				prop = v.flowPropertyFactor.flowProperty;
			}
			Out.put(obj, "flowProperty", prop, conf, Out.REQUIRED_FIELD);
			if (!v.parameterRedefs.isEmpty()) {
				JsonArray redefs = ParameterRedefs.map(v.parameterRedefs, conf);
				Out.put(obj, "parameterRedefs", redefs);
			}
		}
		Out.put(json, "variants", array);
	}

	private JsonObject createRef(ModelType type, Long id) {
		if (id == null)
			return null;
		if (type == null)
			return null;
		return References.create(type, id, conf, true);
	}

}
