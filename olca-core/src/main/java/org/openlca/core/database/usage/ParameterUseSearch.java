package org.openlca.core.database.usage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openlca.core.database.IDatabase;
import org.openlca.core.database.NativeSql;
import org.openlca.core.model.ModelType;
import org.openlca.core.model.ParameterScope;
import org.openlca.core.model.descriptors.CategorizedDescriptor;
import org.openlca.core.model.descriptors.ParameterDescriptor;
import org.openlca.util.Formula;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Searches for the use of parameters in other entities.
 */
public class ParameterUseSearch extends BaseUseSearch<ParameterDescriptor> {

	private final static Logger log = LoggerFactory
			.getLogger(ParameterUseSearch.class);
	private IDatabase database;

	public ParameterUseSearch(IDatabase database) {
		super(database);
		this.database = database;
	}

	@Override
	public List<CategorizedDescriptor> findUses(Set<Long> ids) {
		Set<String> names = getParameterNames(ids);
		List<CategorizedDescriptor> results = findUsesInParameterRedefs(names);
		results.addAll(findUsesInParameters(names));
		return results;
	}

	private List<CategorizedDescriptor> findUsesInParameters(Set<String> names) {
		List<CategorizedDescriptor> results = new ArrayList<>();
		List<ParameterRef> refs = findReferencing(names);
		Set<Long> globals = new HashSet<>();
		Set<Long> processes = new HashSet<>();
		Set<Long> methods = new HashSet<>();
		for (ParameterRef ref : refs)
			if (ref.ownerId == 0l)
				globals.add(ref.id);
			else if (!hasDefinedLocalParameter(ref))
				if (ref.scope == ParameterScope.PROCESS)
					processes.add(ref.ownerId);
				else if (ref.scope == ParameterScope.IMPACT_METHOD)
					methods.add(ref.ownerId);
		results.addAll(loadDescriptors(ModelType.PARAMETER, globals));
		results.addAll(loadDescriptors(ModelType.PROCESS, processes));
		results.addAll(loadDescriptors(ModelType.IMPACT_METHOD, methods));
		return results;
	}

	private boolean hasDefinedLocalParameter(ParameterRef ref) {
		String query = "SELECT count(id) FROM tbl_parameters WHERE f_owner = "
				+ ref.ownerId + " AND name = '" + ref.name + "'";
		Set<Boolean> value = new HashSet<>();
		try {
			NativeSql.on(database).query(query, (result) -> {
				value.add(result.getInt(1) != 0);
				return false;
			});
		} catch (SQLException e) {
			log.error("Error while loading parameters", e);
		}
		return value.contains(true);
	}

	private List<ParameterRef> findReferencing(Set<String> names) {
		String query = "SELECT scope, formula, id, f_owner FROM tbl_parameters";
		List<ParameterRef> refs = new ArrayList<>();
		try {
			NativeSql.on(database).query(query, (result) -> {
				ParameterScope scope = getScope(result.getString(1));
				String formula = result.getString(2);
				long id = result.getLong(3);
				long ownerId = result.getLong(4);
				Set<String> variables = Formula.getVariables(formula);
				for (String name : names)
					if (variables.contains(name))
						refs.add(new ParameterRef(id, ownerId, name, scope));
				return true;
			});
		} catch (SQLException e) {
			log.error("Error while loading parameters", e);
		}
		return refs;
	}

	private ParameterScope getScope(String value) {
		if (value == null)
			return ParameterScope.GLOBAL;
		return ParameterScope.valueOf(value);
	}

	private List<CategorizedDescriptor> findUsesInParameterRedefs(
			Set<String> names) {
		String query = "SELECT f_owner FROM tbl_parameter_redefs WHERE context_type IS NULL AND name IN "
				+ Search.asSqlList(names.toArray());
		Set<Long> ids = new HashSet<>();
		try {
			NativeSql.on(database).query(query, (result) -> {
				ids.add(result.getLong(1));
				return true;
			});
		} catch (SQLException e) {
			log.error("Error while loading parameter redefs by name", e);
		}
		List<CategorizedDescriptor> results = new ArrayList<>();
		results.addAll(loadDescriptors(ModelType.PRODUCT_SYSTEM, ids));
		Set<Long> projectIds = queryForIds("f_project", "tbl_project_variants",
				ids, "id");
		results.addAll(loadDescriptors(ModelType.PROJECT, projectIds));
		return results;
	}

	private Set<String> getParameterNames(Set<Long> ids) {
		String query = "SELECT name FROM tbl_parameters WHERE id IN "
				+ Search.asSqlList(ids);
		Set<String> names = new HashSet<>();
		try {
			NativeSql.on(database).query(query, (result) -> {
				names.add(result.getString(1));
				return true;
			});
		} catch (SQLException e) {
			log.error("Error while loading names of parameters", e);
		}
		return names;
	}

	private class ParameterRef {
		private long id;
		private long ownerId;
		private String name;
		private ParameterScope scope;

		private ParameterRef(long id, long ownerId, String name,
				ParameterScope scope) {
			this.id = id;
			this.ownerId = ownerId;
			this.name = name;
			this.scope = scope;
		}

	}

}
