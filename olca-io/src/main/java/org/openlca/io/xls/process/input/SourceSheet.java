package org.openlca.io.xls.process.input;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.openlca.core.database.SourceDao;
import org.openlca.core.model.ModelType;
import org.openlca.core.model.Source;
import org.openlca.core.model.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

class SourceSheet {

	private Logger log = LoggerFactory.getLogger(getClass());

	private final Config config;
	private final SourceDao dao;

	private SourceSheet(Config config) {
		this.config = config;
		this.dao = new SourceDao(config.database);
	}

	public static void read(Config config) {
		new SourceSheet(config).read();
	}

	private void read() {
		try {
			log.trace("import sources");
			Sheet sheet = config.workbook.getSheet("Sources");
			int row = 1;
			while (true) {
				String uuid = config.getString(sheet, row, 0);
				if (uuid == null || uuid.trim().isEmpty())
					break;
				readSource(uuid, row, sheet);
				row++;
			}
		} catch (Exception e) {
			log.error("failed to read source sheet", e);
		}
	}

	private void readSource(String uuid, int row, Sheet sheet) {
		String name = config.getString(sheet, row, 1);
		String category = config.getString(sheet, row, 3);
		Source source = dao.getForRefId(uuid);
		if (source != null) {
			config.refData.putSource(name, category, source);
			return;
		}
		source = new Source();
		source.setRefId(uuid);
		source.setName(name);
		source.setDescription(config.getString(sheet, row, 2));
		source.setCategory(config.getCategory(category, ModelType.SOURCE));
		setAttributes(row, sheet, source);
		source = dao.insert(source);
		config.refData.putSource(name, category, source);
	}

	private void setAttributes(int row, Sheet sheet, Source source) {
		String version = config.getString(sheet, row, 4);
		source.setVersion(Version.fromString(version).getValue());
		Date lastChange = config.getDate(sheet, row, 5);
		if (lastChange != null)
			source.setLastChange(lastChange.getTime());
		source.setDoi(config.getString(sheet, row, 6));
		source.setTextReference(config.getString(sheet, row, 7));
		Cell yearCell = config.getCell(sheet, row, 8);
		if (yearCell != null && yearCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			double y = yearCell.getNumericCellValue();
			source.setYear((short) y);
		}
	}
}