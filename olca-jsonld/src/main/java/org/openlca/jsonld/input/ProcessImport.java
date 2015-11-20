package org.openlca.jsonld.input;

import org.openlca.core.model.AllocationMethod;
import org.openlca.core.model.Exchange;
import org.openlca.core.model.ModelType;
import org.openlca.core.model.Parameter;
import org.openlca.core.model.Process;
import org.openlca.core.model.ProcessDocumentation;
import org.openlca.core.model.ProcessType;
import org.openlca.core.model.RiskLevel;
import org.openlca.core.model.SocialAspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

class ProcessImport extends BaseImport<Process> {

	private Logger log = LoggerFactory.getLogger(getClass());

	private ProcessImport(String refId, ImportConfig conf) {
		super(ModelType.PROCESS, refId, conf);
	}

	static Process run(String refId, ImportConfig conf) {
		return new ProcessImport(refId, conf).run();
	}

	@Override
	Process map(JsonObject json, long id) {
		if (json == null)
			return null;
		Process p = new Process();
		p.setId(id);
		In.mapAtts(json, p);
		String catId = In.getRefId(json, "category");
		p.setCategory(CategoryImport.run(catId, conf));
		mapProcessType(json, p);
		mapAllocationType(json, p);
		ProcessDocumentation doc = ProcessDocReader.read(json, conf);
		p.setDocumentation(doc);
		String locId = In.getRefId(json, "location");
		if (locId != null)
			p.setLocation(LocationImport.run(locId, conf));
		String curId = In.getRefId(json, "currency");
		if (curId != null)
			p.currency = CurrencyImport.run(curId, conf);
		addParameters(json, p);
		addExchanges(json, p);
		addSocialAspects(json, p);
		return conf.db.put(p);
	}

	private void mapProcessType(JsonObject json, Process p) {
		String type = In.getString(json, "processTyp");
		if (type == null)
			return;
		try {
			p.setProcessType(ProcessType.valueOf(type));
		} catch (Exception e) {
			log.warn("unknown process type " + type, e);
		}
	}

	private void mapAllocationType(JsonObject json, Process p) {
		String type = In.getString(json, "defaultAllocationMethod");
		if (type == null)
			return;
		switch (type) {
		case "CAUSAL_ALLOCATION":
			p.setDefaultAllocationMethod(AllocationMethod.CAUSAL);
			break;
		case "ECONOMIC_ALLOCATION":
			p.setDefaultAllocationMethod(AllocationMethod.ECONOMIC);
			break;
		case "PHYSICAL_ALLOCATION":
			p.setDefaultAllocationMethod(AllocationMethod.PHYSICAL);
			break;
		default:
			log.warn("unknown allocation type " + type);
		}
	}

	private void addParameters(JsonObject json, Process p) {
		JsonArray parameters = In.getArray(json, "parameters");
		if (parameters == null)
			return;
		for (JsonElement e : parameters) {
			if (!e.isJsonObject())
				continue;
			JsonObject o = e.getAsJsonObject();
			String refId = In.getString(o, "@id");
			ParameterImport pi = new ParameterImport(refId, conf);
			Parameter parameter = new Parameter();
			pi.mapFields(o, parameter);
			p.getParameters().add(parameter);
		}
	}

	private void addExchanges(JsonObject json, Process p) {
		JsonArray exchanges = In.getArray(json, "exchanges");
		if (exchanges == null)
			return;
		for (JsonElement e : exchanges) {
			if (!e.isJsonObject())
				continue;
			JsonObject o = e.getAsJsonObject();
			Exchange exchange = Exchanges.map(o, conf);
			p.getExchanges().add(exchange);
			boolean isRef = In.getBool(o, "quantitativeReference", false);
			if (isRef)
				p.setQuantitativeReference(exchange);
		}
	}

	private void addSocialAspects(JsonObject json, Process p) {
		JsonArray aspects = In.getArray(json, "socialAspects");
		if (aspects == null)
			return;
		for (JsonElement a : aspects) {
			if (!a.isJsonObject())
				continue;
			JsonObject o = a.getAsJsonObject();
			SocialAspect aspect = aspect(o);
			p.socialAspects.add(aspect);
		}
	}

	private SocialAspect aspect(JsonObject json) {
		SocialAspect a = new SocialAspect();
		a.indicator = SocialIndicatorImport.run(
				In.getRefId(json, "socialIndicator"), conf);
		a.comment = In.getString(json, "comment");
		a.quality = In.getString(json, "quality");
		a.rawAmount = In.getString(json, "rawAmount");
		a.activityValue = In.getDouble(json, "activityValue", 0d);
		String riskLevel = In.getString(json, "riskLevel");
		if (riskLevel != null)
			a.riskLevel = RiskLevel.valueOf(riskLevel);
		a.source = SourceImport.run(In.getRefId(json, "source"), conf);
		return a;
	}

}