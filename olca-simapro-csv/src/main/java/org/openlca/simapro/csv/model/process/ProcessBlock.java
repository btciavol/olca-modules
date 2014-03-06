package org.openlca.simapro.csv.model.process;

import java.util.ArrayList;
import java.util.List;

import org.openlca.simapro.csv.model.ElementaryExchangeRow;
import org.openlca.simapro.csv.model.annotations.BlockModel;
import org.openlca.simapro.csv.model.annotations.SectionRows;
import org.openlca.simapro.csv.model.annotations.SectionValue;
import org.openlca.simapro.csv.model.enums.BoundaryWithNature;
import org.openlca.simapro.csv.model.enums.CutOffRule;
import org.openlca.simapro.csv.model.enums.Geography;
import org.openlca.simapro.csv.model.enums.ProcessAllocation;
import org.openlca.simapro.csv.model.enums.ProcessCategory;
import org.openlca.simapro.csv.model.enums.ProcessType;
import org.openlca.simapro.csv.model.enums.Representativeness;
import org.openlca.simapro.csv.model.enums.Status;
import org.openlca.simapro.csv.model.enums.Substitution;
import org.openlca.simapro.csv.model.enums.Technology;
import org.openlca.simapro.csv.model.enums.TimePeriod;

@BlockModel("Process")
public class ProcessBlock {

	@SectionValue("Category type")
	private ProcessCategory category;

	@SectionValue("Process identifier")
	private String identifier;

	@SectionValue("Type")
	private ProcessType processType;

	@SectionValue("Process name")
	private String name;

	@SectionValue("Status")
	private Status status;

	@SectionValue("Time period")
	private TimePeriod time;

	@SectionValue("Geography")
	private Geography geography;

	@SectionValue("Technology")
	private Technology technology;

	@SectionValue("Representativeness")
	private Representativeness representativeness;

	@SectionValue("Multiple output allocation")
	private ProcessAllocation allocation;

	@SectionValue("Substitution allocation")
	private Substitution substitution;

	@SectionValue("Cut off rules")
	private CutOffRule cutoff;

	@SectionValue("Capital goods")
	private String capitalGoods;

	@SectionValue("Boundary with nature")
	private BoundaryWithNature boundaryWithNature;

	// TODO: other fields

	@SectionRows("Products")
	private List<ProductOutputRow> products = new ArrayList<>();

	// TODO: waste treatment rows
	// TODO: avoided products
	// TODO: product inpouts

	@SectionRows("Resources")
	private List<ElementaryExchangeRow> resources = new ArrayList<>();

	@SectionRows("Emissions to air")
	private List<ElementaryExchangeRow> emissionsToAir = new ArrayList<>();

	@SectionRows("Emissions to water")
	private List<ElementaryExchangeRow> emissionsToWater = new ArrayList<>();

	@SectionRows("Emissions to soil")
	private List<ElementaryExchangeRow> emissionsToSoil = new ArrayList<>();

	@SectionRows("Final waste flows")
	private List<ElementaryExchangeRow> finalWasteFlows = new ArrayList<>();

	@SectionRows("Non material emissions")
	private List<ElementaryExchangeRow> nonMaterialEmissions = new ArrayList<>();

	@SectionRows("Social issues")
	private List<ElementaryExchangeRow> socialIssues = new ArrayList<>();

	@SectionRows("Economic issues")
	private List<ElementaryExchangeRow> economicIssues = new ArrayList<>();

	// TODO: waste to treatment

	public List<ProductOutputRow> getProducts() {
		return products;
	}

	public List<ElementaryExchangeRow> getResources() {
		return resources;
	}

	public List<ElementaryExchangeRow> getEmissionsToAir() {
		return emissionsToAir;
	}

	public List<ElementaryExchangeRow> getEmissionsToWater() {
		return emissionsToWater;
	}

	public List<ElementaryExchangeRow> getEmissionsToSoil() {
		return emissionsToSoil;
	}

	public List<ElementaryExchangeRow> getFinalWasteFlows() {
		return finalWasteFlows;
	}

	public List<ElementaryExchangeRow> getNonMaterialEmissions() {
		return nonMaterialEmissions;
	}

	public List<ElementaryExchangeRow> getSocialIssues() {
		return socialIssues;
	}

	public List<ElementaryExchangeRow> getEconomicIssues() {
		return economicIssues;
	}

	public ProcessCategory getCategory() {
		return category;
	}

	public void setCategory(ProcessCategory category) {
		this.category = category;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public ProcessType getProcessType() {
		return processType;
	}

	public void setProcessType(ProcessType processType) {
		this.processType = processType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public TimePeriod getTime() {
		return time;
	}

	public void setTime(TimePeriod time) {
		this.time = time;
	}

	public Geography getGeography() {
		return geography;
	}

	public void setGeography(Geography geography) {
		this.geography = geography;
	}

	public Technology getTechnology() {
		return technology;
	}

	public void setTechnology(Technology technology) {
		this.technology = technology;
	}

	public Representativeness getRepresentativeness() {
		return representativeness;
	}

	public void setRepresentativeness(Representativeness representativeness) {
		this.representativeness = representativeness;
	}

	public ProcessAllocation getAllocation() {
		return allocation;
	}

	public void setAllocation(ProcessAllocation allocation) {
		this.allocation = allocation;
	}

	public Substitution getSubstitution() {
		return substitution;
	}

	public void setSubstitution(Substitution substitution) {
		this.substitution = substitution;
	}

	public CutOffRule getCutoff() {
		return cutoff;
	}

	public void setCutoff(CutOffRule cutoff) {
		this.cutoff = cutoff;
	}

	public String getCapitalGoods() {
		return capitalGoods;
	}

	public void setCapitalGoods(String capitalgoods) {
		this.capitalGoods = capitalgoods;
	}

	public BoundaryWithNature getBoundaryWithNature() {
		return boundaryWithNature;
	}

	public void setBoundaryWithNature(BoundaryWithNature boundarywithnature) {
		this.boundaryWithNature = boundarywithnature;
	}
}
