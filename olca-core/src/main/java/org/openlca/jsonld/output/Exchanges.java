package org.openlca.jsonld.output;

import org.openlca.core.database.ProcessDao;
import org.openlca.core.model.Exchange;
import org.openlca.core.model.FlowProperty;
import org.openlca.core.model.ModelType;

import com.google.gson.JsonObject;

class Exchanges {

	static boolean map(Exchange e, JsonObject obj, ExportConfig conf) {
		if (e == null || obj == null)
			return false;
		Out.put(obj, "@type", Exchange.class.getSimpleName());
		Out.put(obj, "avoidedProduct", e.isAvoided);
		Out.put(obj, "input", e.isInput);
		Out.put(obj, "baseUncertainty", e.baseUncertainty);
		Out.put(obj, "amount", e.amount);
		Out.put(obj, "amountFormula", e.formula);
		Out.put(obj, "dqEntry", e.dqEntry);
		Out.put(obj, "description", e.description);
		Out.put(obj, "costFormula", e.costFormula);
		Out.put(obj, "costValue", e.costs);
		Out.put(obj, "currency", e.currency, conf);
		Out.put(obj, "internalId", e.internalId);
		mapRefs(e, obj, conf);
		return true;
	}

	private static void mapRefs(Exchange e, JsonObject obj, ExportConfig conf) {
		Long pId = e.defaultProviderId;
		JsonObject p = null;
		if (conf.exportProviders)
			p = References.create(ModelType.PROCESS, pId, conf, false);
		else if (conf.db != null)
			p = References.create(new ProcessDao(conf.db).getDescriptor(pId), conf);
		Out.put(obj, "defaultProvider", p);
		Out.put(obj, "flow", e.flow, conf, Out.REQUIRED_FIELD);
		if (e.flow != null) {
			JsonObject flow = obj.get("flow").getAsJsonObject();
			Out.put(flow, "flowType", e.flow.flowType);
		}
		Out.put(obj, "unit", e.unit, conf, Out.REQUIRED_FIELD);
		FlowProperty property = null;
		if (e.flowPropertyFactor != null)
			property = e.flowPropertyFactor.flowProperty;
		Out.put(obj, "flowProperty", property, conf, Out.REQUIRED_FIELD);
		Out.put(obj, "uncertainty", Uncertainties.map(e.uncertainty));
	}
}
