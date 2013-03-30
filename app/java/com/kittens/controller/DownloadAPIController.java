package com.kittens.controller;

import com.kittens.database.User;
import com.kittens.Utils;

import java.io.IOException;
import java.lang.String;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;

public class DownloadAPIController extends BaseController {

	// the version of this object
	private static final long serialVersionUID = 0L;

	/**
	 * Handle GET requests.
	 */
	@Override public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (database == null) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		final User currentSessionUser = Utils.getUserFromRequest(request);
		if (currentSessionUser == null) {
			response.sendRedirect(Utils.APP_ROOT);
			return;
		}
		final String UUID = request.getParameter("uuid");
		if (UUID == null) {
			// bad request
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		try {
			ServletOutputStream servletOut = response.getOutputStream();
			ZipOutputStream zipOut = new ZipOutputStream(servletOut);
			// add the CSV file to the Zip file
			ZipEntry csvFile = new ZipEntry(String.format("%s.csv", UUID));
			zipOut.putNextEntry(csvFile);
			String datasetCsv = database.getDataset(UUID).toCSV();
			zipOut.write(datasetCsv.getBytes(Charset.forName("UTF-8")));
			zipOut.closeEntry();
			// add the CSV file to the Zip file
			ZipEntry tsvFile = new ZipEntry(String.format("%s.tsv", UUID));
			zipOut.putNextEntry(tsvFile);
			String datasetTsv = database.getDataset(UUID).toTSV();
			zipOut.write(datasetTsv.getBytes(Charset.forName("UTF-8")));
			zipOut.closeEntry();
			// write it out
			zipOut.close();
			response.setContentType("application/zip");
			servletOut.flush();
		}
		catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
		// return;
	}

}
