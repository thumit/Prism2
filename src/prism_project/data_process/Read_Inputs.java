/*******************************************************************************
 * Copyright (C) 2016-2018 PRISM Development Team
 * 
 * PRISM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * PRISM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with PRISM.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package prism_project.data_process;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class Read_Inputs {

	public List get_Included(List list, String delimited, int cols) {
		int IncCol =  cols-1;
		list.remove(0);	// Remove the first row (Column names)
		String first = list.get(0).toString();
		String[] firstItems = first.split(delimited);
		int colNums = firstItems.length;
		if (colNums == cols) {
			for (int i = 0; i < list.size(); i++) {
				String item = list.get(i).toString();
				String[] items = item.split(delimited);
				if (items[IncCol].equals("false")) {
					list.remove(i);
				}
			}
		}
		return list;
	}
	
	//-------------------------------------------------------------------------------------------------------------------------------------------------	
	//For input_01_general_inputs.txt
	private int gi_totalRows, gi_totalColumns;
	private String[][] gi_value;

	public void read_general_inputs(File file) {
		String delimited = "\t";		// tab delimited
				
		try {		
			// All lines to be in array
			List<String> list;
			list = Files.readAllLines(Paths.get(file.getAbsolutePath()), StandardCharsets.UTF_8);
			list.remove(0);	// Remove the first row (Column names)
			String[] a = list.toArray(new String[list.size()]);
								
			gi_totalRows = a.length;
			gi_totalColumns = a[0].split(delimited).length;		// a[0].split(delimited) = String[] of the first row (this is the row below the column headers row which was removed already)					
			gi_value = new String[gi_totalRows][gi_totalColumns];
		
			// read all values from all rows and columns
			for (int i = 0; i < gi_totalRows; i++) {
				String[] rowValue = a[i].split(delimited);
				for (int j = 0; j < gi_totalColumns; j++) {
					gi_value[i][j] = rowValue[j].replaceAll("\\s+", "");
				}
			}

		} catch (IOException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	public int get_total_periods() {
		return Integer.parseInt(gi_value[0][1]);
	}
	
	public int get_total_replacing_disturbances() {
		return Integer.parseInt(gi_value[1][1]);
	}

	public double get_discount_rate() {
		return Double.parseDouble(gi_value[2][1]);
	}

	public String get_solver() {
		return gi_value[3][1].toString();
	}

	public int get_solving_time() {
		return Integer.parseInt(gi_value[4][1]);
	}

	public boolean get_export_problem() {
		return Boolean.parseBoolean(gi_value[5][1]);
	}

	public boolean get_export_solution() {
		return Boolean.parseBoolean(gi_value[6][1]);
	}

	//-------------------------------------------------------------------------------------------------------------------------------------------------	
	//For input_02_model_strata: must be read before silviculture_method
	private int MO_totalRows, MO_totalColumns;
	private String[][] MO_value;
		
	public void read_model_strata(File file) {
		String delimited = "\t";		// tab delimited
				
		try {		
			// All lines to be in array
			List<String> list;
			list = Files.readAllLines(Paths.get(file.getAbsolutePath()), StandardCharsets.UTF_8);
			list.remove(0);	// Remove the first row (Column names)
			String[] a = list.toArray(new String[list.size()]);
						
			MO_totalRows = a.length;
			MO_totalColumns = a[0].split(delimited).length;		// a[0].split(delimited) = String[] of the first row (this is the row below the column headers row which was removed already)				
			MO_value = new String[MO_totalRows][MO_totalColumns];
		
			// read all values from all rows and columns
			for (int i = 0; i < MO_totalRows; i++) {
				String[] rowValue = a[i].split(delimited);
				for (int j = 0; j < MO_totalColumns; j++) {
					MO_value[i][j] = rowValue[j].replaceAll("\\s+", "");
				}
			}

		} catch (IOException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	public String[][] get_MO_Values() {
		return MO_value;
	}

	public int get_MO_TotalRows() {
		return MO_totalRows;
	}
	
	public int get_MO_TotalColumns() {
		return MO_totalColumns;
	}
	
	public List<String> get_model_strata() {
		List<String> modeled_strata = new ArrayList<String>();
		for (int i = 0; i < MO_totalRows; i++) {		
			String combined_name = MO_value[i][1] + MO_value[i][2] + MO_value[i][3] + MO_value[i][4] + MO_value[i][5] + MO_value[i][6];
			modeled_strata.add(combined_name);
		}
		return modeled_strata;
	}
	
	public List<String> get_model_strata_without_sizeclass() {	// Note this is replaced in Panel_Solve because we need all the s5 --> use the below: get_model_strata_without_sizeclass_and_covertype + a loop add all s5
		List<String> model_strata_without_sizeclass = new ArrayList<String>();
		for (int i = 0; i < MO_totalRows; i++) {		
			String combined_name = MO_value[i][1] + MO_value[i][2] + MO_value[i][3] + MO_value[i][4] + MO_value[i][5];
			if (!model_strata_without_sizeclass.contains(combined_name)) {	// only add to list if list does not contain the value
				model_strata_without_sizeclass.add(combined_name);
			}
		}
		return model_strata_without_sizeclass;
	}	
	
	public List<String> get_model_strata_without_sizeclass_and_covertype() {
		List<String> model_strata_without_sizeclass_and_covertype = new ArrayList<String>();
		for (int i = 0; i < MO_totalRows; i++) {		
			String combined_name = MO_value[i][1] + MO_value[i][2] + MO_value[i][3] + MO_value[i][4];			
			if (!model_strata_without_sizeclass_and_covertype.contains(combined_name)) {	// only add to list if list does not contain the value
				model_strata_without_sizeclass_and_covertype.add(combined_name);
			}	
		}
		return model_strata_without_sizeclass_and_covertype;
	}
	
	//-------------------------------------------------------------------------------------------------------------------------------------------------	
	//For input_03_non_ea_management : must be read after model_strata
	private int nonea_totalRows, nonea_totalColumns;
	private String[][] nonea_value;
	private List<List<String>> nonea_method_choice_for_strata;
	private List<List<String>> nonea_method_choice_for_strata_without_sizeclass;

	public void read_non_ea_management(File file) {
		String delimited = "\t";		// tab delimited
				
		try {		
			// All lines to be in array
			List<String> list;
			list = Files.readAllLines(Paths.get(file.getAbsolutePath()), StandardCharsets.UTF_8);
			list = get_Included(list, delimited, 5);
			String[] a = list.toArray(new String[list.size()]);

								
			nonea_totalRows = a.length;
			nonea_totalColumns = a[0].split(delimited).length;		// a[0].split(delimited) = String[] of the first row (this is the row below the column headers row which was removed already)	
			nonea_value = new String[nonea_totalRows][nonea_totalColumns];
		
			// read all values from all rows and columns
			for (int i = 0; i < nonea_totalRows; i++) {		
				String[] rowValue = a[i].split(delimited);		
				for (int j = 0; j < nonea_totalColumns; j++) {
					nonea_value[i][j] = rowValue[j];
				}
			}
			System.out.println(list);
		} catch (IOException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}
	
	public List<List<String>> get_nonea_static_identifiers_in_row(int row) {
		List<List<String>> nonea_static_identifiers = new ArrayList<List<String>>();
		
		//Read the whole cell into array
		String[] staticLayer_Info = nonea_value[row][2].split(";");
		int total_staticIdentifiers = staticLayer_Info.length;
		
		//Get all static Identifiers to be in the list
		for (int i = 0; i < total_staticIdentifiers; i++) {		//6 first identifiers is strata 6 layers (layer 0 to 5)		
			List<String> thisIdentifier = new ArrayList<String>();
			
			String[] identifierElements = staticLayer_Info[i].split("\\s+");				//space delimited
			for (int j = 1; j < identifierElements.length; j++) {		//Ignore the first element which is the identifier index, so we loop from 1 not 0
				thisIdentifier.add(identifierElements[j].replaceAll("\\s+",""));		//Add element name, if name has spaces then remove all the spaces
			}
			
			nonea_static_identifiers.add(thisIdentifier);
		}
			
		return nonea_static_identifiers;
	}	
	
	public List<List<String>> get_nonea_method_choice_in_row(int row) {
		List<List<String>> nonea_method_choice = new ArrayList<List<String>>();
		
		//Read the whole cell into array
		String[] method_choice_info = nonea_value[row][3].split(";");
		int total_method_choice = method_choice_info.length;
		
		//Get all method and choice to be in the list
		for (int i = 0; i < total_method_choice; i++) {		// only 2: method and choice	
			List<String> thisIdentifier = new ArrayList<String>();
			
			String[] identifierElements = method_choice_info[i].split("\\s+");				//space delimited
			for (int j = 1; j < identifierElements.length; j++) {		//Ignore the first element which is the identifier index, so we loop from 1 not 0
				thisIdentifier.add(identifierElements[j].replaceAll("\\s+",""));		//Add element name, if name has spaces then remove all the spaces
			}
			
			nonea_method_choice.add(thisIdentifier);
		}
			
		return nonea_method_choice;
	}
			
	public void populate_nonea_lists(List<String> model_strata, List<String> model_strata_without_sizeclass, List<List<String>> all_layers) {	
		// Calculate this first to avoid calculating it in the below loops------------------- ------------------------------------
		List<List<String>>[] nonea_static_identifiers = new ArrayList[nonea_totalRows]; 
		List<List<String>>[] nonea_method_choice  = new ArrayList[nonea_totalRows];
		for (int row = 0; row < nonea_totalRows; row++) {	// load each row
			nonea_static_identifiers[row] = get_nonea_static_identifiers_in_row(row);			
			nonea_method_choice[row] = get_nonea_method_choice_in_row(row);
		}
		

		
		// existing 6 layers ------------------------------------ ------------------------------------ ------------------------------------
		nonea_method_choice_for_strata = new ArrayList<List<String>>();
		for (int i = 0; i < model_strata.size(); i++) {
			nonea_method_choice_for_strata.add(new ArrayList<String>());
		}
		
		for (int strata_id = 0; strata_id < model_strata.size(); strata_id++) {
			String strata = model_strata.get(strata_id);
			String layer1 = strata.substring(0, 1);
			String layer2 = strata.substring(1, 2);
			String layer3 = strata.substring(2, 3);
			String layer4 = strata.substring(3, 4);
			String layer5 = strata.substring(4, 5);
			String layer6 = strata.substring(5, 6);
		
			for (int row = 0; row < nonea_totalRows; row++) {	// load each row
				List<List<String>> static_identifiers = nonea_static_identifiers[row];			
				List<List<String>> method_choice = nonea_method_choice[row];	
				
				// The below check also implements Speed Boost RRB9
				if (	(static_identifiers.get(0).size() == all_layers.get(0).size() || Collections.binarySearch(static_identifiers.get(0), layer1) >= 0) && 
						(static_identifiers.get(1).size() == all_layers.get(1).size() || Collections.binarySearch(static_identifiers.get(1), layer2) >= 0) && 
						(static_identifiers.get(2).size() == all_layers.get(2).size() || Collections.binarySearch(static_identifiers.get(2), layer3) >= 0) && 
						(static_identifiers.get(3).size() == all_layers.get(3).size() || Collections.binarySearch(static_identifiers.get(3), layer4) >= 0) && 
						(static_identifiers.get(4).size() == all_layers.get(4).size() || Collections.binarySearch(static_identifiers.get(4), layer5) >= 0) && 
						(static_identifiers.get(5).size() == all_layers.get(5).size() || Collections.binarySearch(static_identifiers.get(5), layer6) >= 0)	)	
				{
					for (String method : method_choice.get(0)) {
						for (String choice : method_choice.get(1)) {
							if (!nonea_method_choice_for_strata.get(strata_id).contains(method + " " + choice)) {
								nonea_method_choice_for_strata.get(strata_id).add(method + " " + choice);
							}
						}
					}
				}
				
//				// Without RRB9 --> no need all_layers
//				if (	Collections.binarySearch(static_identifiers.get(0), layer1) >= 0 && 
//						Collections.binarySearch(static_identifiers.get(1), layer2) >= 0 && 
//						Collections.binarySearch(static_identifiers.get(2), layer3) >= 0 && 
//						Collections.binarySearch(static_identifiers.get(3), layer4) >= 0 && 
//						Collections.binarySearch(static_identifiers.get(4), layer5) >= 0 && 
//						Collections.binarySearch(static_identifiers.get(5), layer6) >= 0)	
//				{
//					for (String method : method_choice.get(0)) {
//						for (String choice : method_choice.get(1)) {
//							if (!nonea_method_choice_for_strata.get(strata_id).contains(method + " " + choice)) {
//								nonea_method_choice_for_strata.get(strata_id).add(method + " " + choice);
//							}
//						}
//					}
//				}
			}
		}
		
		for (List<String> i : nonea_method_choice_for_strata) {
			Collections.sort(i);	// sort to use Binary Search when needed (in Panel_Solve)
		}
		
		
		
		// regeneration 5 layers ------------------------------------ ------------------------------------ ------------------------------------
		nonea_method_choice_for_strata_without_sizeclass = new ArrayList<List<String>>();
		for (int i = 0; i < model_strata_without_sizeclass.size(); i++) {
			nonea_method_choice_for_strata_without_sizeclass.add(new ArrayList<String>());
		}
		
		for (int strata_5layers_id = 0; strata_5layers_id < model_strata_without_sizeclass.size(); strata_5layers_id++) {
			String strata_5layers = model_strata_without_sizeclass.get(strata_5layers_id);
			String layer1 = strata_5layers.substring(0, 1);
			String layer2 = strata_5layers.substring(1, 2);
			String layer3 = strata_5layers.substring(2, 3);
			String layer4 = strata_5layers.substring(3, 4);
			String layer5 = strata_5layers.substring(4, 5);
		
			for (int row = 0; row < nonea_totalRows; row++) {	// load each row
				List<List<String>> static_identifiers = nonea_static_identifiers[row];			
				List<List<String>> method_choice = nonea_method_choice[row];	
				
				// The below check also implements Speed Boost RRB9
				if (	(static_identifiers.get(0).size() == all_layers.get(0).size() || Collections.binarySearch(static_identifiers.get(0), layer1) >= 0) && 
						(static_identifiers.get(1).size() == all_layers.get(1).size() || Collections.binarySearch(static_identifiers.get(1), layer2) >= 0) && 
						(static_identifiers.get(2).size() == all_layers.get(2).size() || Collections.binarySearch(static_identifiers.get(2), layer3) >= 0) && 
						(static_identifiers.get(3).size() == all_layers.get(3).size() || Collections.binarySearch(static_identifiers.get(3), layer4) >= 0) && 
						(static_identifiers.get(4).size() == all_layers.get(4).size() || Collections.binarySearch(static_identifiers.get(4), layer5) >= 0) )	
				{
					for (String method : method_choice.get(0)) {
						for (String choice : method_choice.get(1)) {
							if (!nonea_method_choice_for_strata_without_sizeclass.get(strata_5layers_id).contains(method + " " + choice)) {
								nonea_method_choice_for_strata_without_sizeclass.get(strata_5layers_id).add(method + " " + choice);
							}
						}
					}	
				}
				
//				// Without RRB9 --> no need all_layers
//				if (	Collections.binarySearch(static_identifiers.get(0), layer1) >= 0 && 
//						Collections.binarySearch(static_identifiers.get(1), layer2) >= 0 && 
//						Collections.binarySearch(static_identifiers.get(2), layer3) >= 0 && 
//						Collections.binarySearch(static_identifiers.get(3), layer4) >= 0 && 
//						Collections.binarySearch(static_identifiers.get(4), layer5) >= 0	)	
//				{
//					for (String method : method_choice.get(0)) {
//						for (String choice : method_choice.get(1)) {
//							if (!nonea_method_choice_for_strata_without_sizeclass.get(strata_5layers_id).contains(method + " " + choice)) {
//								nonea_method_choice_for_strata_without_sizeclass.get(strata_5layers_id).add(method + " " + choice);
//							}
//						}
//					}	
//				}
			}
		}	
		
		for (List<String> i : nonea_method_choice_for_strata_without_sizeclass) {
			Collections.sort(i);	// sort to use Binary Search when needed (in Panel_Solve)
		}
	}	
	
	public int get_nonea_totalRows() {
		return nonea_totalRows;
	}
	
	public List<List<String>> get_nonea_method_choice_for_strata() {
		return nonea_method_choice_for_strata;
	}
	
	public List<List<String>> get_nonea_method_choice_for_strata_without_sizeclass() {
		return nonea_method_choice_for_strata_without_sizeclass;
	}
		
	//-------------------------------------------------------------------------------------------------------------------------------------------------	
	//For input_04_ea_management
	private int ea_totalRows, ea_totalColumns;
	private String[][] ea_value;
	private List<List<String>> ea_conversion_and_rotation_for_strata;
	private List<List<String>> ea_conversion_and_rotation_for_strata_without_sizeclass;

	public void read_ea_management(File file) {
		String delimited = "\t";		// tab delimited
				
		try {		
			// All lines to be in array
			List<String> list;
			list = Files.readAllLines(Paths.get(file.getAbsolutePath()), StandardCharsets.UTF_8);
			list = get_Included(list, delimited, 5);
			String[] a = list.toArray(new String[list.size()]);
								
			ea_totalRows = a.length;
			ea_totalColumns = a[0].split(delimited).length;		// a[0].split(delimited) = String[] of the first row (this is the row below the column headers row which was removed already)	
			ea_value = new String[ea_totalRows][ea_totalColumns];
		
			// read all values from all rows and columns
			for (int i = 0; i < ea_totalRows; i++) {		
				String[] rowValue = a[i].split(delimited);		
				for (int j = 0; j < ea_totalColumns; j++) {
					ea_value[i][j] = rowValue[j];
				}
			}
		} catch (IOException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}
	
	public List<List<String>> get_ea_static_identifiers_in_row(int row) {
		List<List<String>> ea_static_identifiers = new ArrayList<List<String>>();
		
		//Read the whole cell into array
		String[] staticLayer_Info = ea_value[row][2].split(";");
		int total_staticIdentifiers = staticLayer_Info.length;
		
		//Get all static Identifiers to be in the list
		for (int i = 0; i < total_staticIdentifiers; i++) {		//6 first identifiers is strata 6 layers (layer 0 to 5)		
			List<String> thisIdentifier = new ArrayList<String>();
			
			String[] identifierElements = staticLayer_Info[i].split("\\s+");				//space delimited
			for (int j = 1; j < identifierElements.length; j++) {		//Ignore the first element which is the identifier index, so we loop from 1 not 0
				thisIdentifier.add(identifierElements[j].replaceAll("\\s+",""));		//Add element name, if name has spaces then remove all the spaces
			}
			
			ea_static_identifiers.add(thisIdentifier);
		}
			
		return ea_static_identifiers;
	}	
	
	public List<List<String>> get_ea_conversion_and_rotation_in_row(int row) {
		List<List<String>> ea_conversion_and_rotation = new ArrayList<List<String>>();		// 3 elements
		
		
		// All lines except the 1st line to be in a list;		
		String[] list = ea_value[row][3].split(";");	// each element of this list is a row as seen in table4a in Prism GUI
		
		List<String> covertype_conversions_and_existing_rotation_ages_list = new ArrayList<String>();
		List<String> covertype_conversions_and_regeneration_rotation_ages_list = new ArrayList<String>();
		List<String> covertype_conversions_list = new ArrayList<String>();
		
		for (int i = 0; i < list.length; i++) {
			String[] values = list[i].split("\\s+");
			if (values[6].equals("true")) {	// this is the implementation boolean value as seen in table4a in Prism GUI
				covertype_conversions_list.add(values[0] + " " + values[1]);	// layer5  layer5_regen
				int e_RA_min = Integer.parseInt(values[2]);
				int e_RA_max = Integer.parseInt(values[3]);
				int r_RA_min = Integer.parseInt(values[4]);
				int r_RA_max = Integer.parseInt(values[5]);

				for (int age = e_RA_min; age <= e_RA_max; age++) {
					String temp = values[0] + " " + values[1] + " " + age;
					covertype_conversions_and_existing_rotation_ages_list.add(temp);
				}
				
				for (int age = r_RA_min; age <= r_RA_max; age++) {
					String temp = values[0] + " " + values[1] + " " + age;
					covertype_conversions_and_regeneration_rotation_ages_list.add(temp);
				}
			}
		}
		
		
		ea_conversion_and_rotation.add(covertype_conversions_and_existing_rotation_ages_list);		// add 1st element
		ea_conversion_and_rotation.add(covertype_conversions_and_regeneration_rotation_ages_list);		// add 2nd element
		ea_conversion_and_rotation.add(covertype_conversions_list);		// add 3rd element
		return ea_conversion_and_rotation;
	}
			
	public void populate_ea_lists(List<String> model_strata, List<String> model_strata_without_sizeclass, List<List<String>> all_layers) {	
		// Calculate this first to avoid calculating it in the below loops------------------- ------------------------------------
		List<List<String>>[] ea_static_identifiers = new ArrayList[ea_totalRows]; 
		List<List<String>>[] ea_conversion_and_rotation  = new ArrayList[ea_totalRows];
		for (int row = 0; row < ea_totalRows; row++) {	// load each row
			ea_static_identifiers[row] = get_ea_static_identifiers_in_row(row);			
			ea_conversion_and_rotation[row] = get_ea_conversion_and_rotation_in_row(row);
		}
		

		
		// existing 6 layers ------------------------------------ ------------------------------------ ------------------------------------
		ea_conversion_and_rotation_for_strata = new ArrayList<List<String>>();
		for (int i = 0; i < model_strata.size(); i++) {
			ea_conversion_and_rotation_for_strata.add(new ArrayList<String>());
		}
		
		for (int strata_id = 0; strata_id < model_strata.size(); strata_id++) {
			String strata = model_strata.get(strata_id);
			String layer1 = strata.substring(0, 1);
			String layer2 = strata.substring(1, 2);
			String layer3 = strata.substring(2, 3);
			String layer4 = strata.substring(3, 4);
			String layer5 = strata.substring(4, 5);
			String layer6 = strata.substring(5, 6);
		
			for (int row = 0; row < ea_totalRows; row++) {	// load each row
				List<List<String>> static_identifiers = ea_static_identifiers[row];			
				List<List<String>> conversion_and_rotation = ea_conversion_and_rotation[row];	
				
				// The below check also implements Speed Boost RRB9
				if (	(static_identifiers.get(0).size() == all_layers.get(0).size() || Collections.binarySearch(static_identifiers.get(0), layer1) >= 0) && 
						(static_identifiers.get(1).size() == all_layers.get(1).size() || Collections.binarySearch(static_identifiers.get(1), layer2) >= 0) && 
						(static_identifiers.get(2).size() == all_layers.get(2).size() || Collections.binarySearch(static_identifiers.get(2), layer3) >= 0) && 
						(static_identifiers.get(3).size() == all_layers.get(3).size() || Collections.binarySearch(static_identifiers.get(3), layer4) >= 0) && 
						(static_identifiers.get(4).size() == all_layers.get(4).size() || Collections.binarySearch(static_identifiers.get(4), layer5) >= 0) && 
						(static_identifiers.get(5).size() == all_layers.get(5).size() || Collections.binarySearch(static_identifiers.get(5), layer6) >= 0)	)	
				{
					for (String s5_s5r_a : conversion_and_rotation.get(0)) {	// this list is covertype_conversions_and_existing_rotation_ages_list);
							if (!ea_conversion_and_rotation_for_strata.get(strata_id).contains(s5_s5r_a)) {
								ea_conversion_and_rotation_for_strata.get(strata_id).add(s5_s5r_a);
							}
					}
				}
				
//				// Without RRB9 --> no need all_layers
//				if (	Collections.binarySearch(static_identifiers.get(0), layer1) >= 0 && 
//						Collections.binarySearch(static_identifiers.get(1), layer2) >= 0 && 
//						Collections.binarySearch(static_identifiers.get(2), layer3) >= 0 &&
//						Collections.binarySearch(static_identifiers.get(3), layer4) >= 0 && 
//						Collections.binarySearch(static_identifiers.get(4), layer5) >= 0 && 
//						Collections.binarySearch(static_identifiers.get(5), layer6) >= 0	)	
//				{
//					for (String s5_s5r_a : conversion_and_rotation.get(0)) {	// this list is covertype_conversions_and_existing_rotation_ages_list);
//							if (!ea_conversion_and_rotation_for_strata.get(strata_id).contains(s5_s5r_a)) {
//								ea_conversion_and_rotation_for_strata.get(strata_id).add(s5_s5r_a);
//							}
//					}
//				}
			}
		}
		
		for (List<String> i : ea_conversion_and_rotation_for_strata) {
			Collections.sort(i);	// sort to use Binary Search when needed (in Panel_Solve)
		}
		
		
		
		// regeneration 5 layers ------------------------------------ ------------------------------------ ------------------------------------
		ea_conversion_and_rotation_for_strata_without_sizeclass = new ArrayList<List<String>>();
		for (int i = 0; i < model_strata_without_sizeclass.size(); i++) {
			ea_conversion_and_rotation_for_strata_without_sizeclass.add(new ArrayList<String>());
		}
		
		for (int strata_5layers_id = 0; strata_5layers_id < model_strata_without_sizeclass.size(); strata_5layers_id++) {
			String strata_5layers = model_strata_without_sizeclass.get(strata_5layers_id);
			String layer1 = strata_5layers.substring(0, 1);
			String layer2 = strata_5layers.substring(1, 2);
			String layer3 = strata_5layers.substring(2, 3);
			String layer4 = strata_5layers.substring(3, 4);
			String layer5 = strata_5layers.substring(4, 5);
		
			for (int row = 0; row < ea_totalRows; row++) {	// load each row
				List<List<String>> static_identifiers = ea_static_identifiers[row];			
				List<List<String>> conversion_and_rotation = ea_conversion_and_rotation[row];	
				
				// The below check also implements Speed Boost RRB9
				if (	(static_identifiers.get(0).size() == all_layers.get(0).size() || Collections.binarySearch(static_identifiers.get(0), layer1) >= 0) && 
						(static_identifiers.get(1).size() == all_layers.get(1).size() || Collections.binarySearch(static_identifiers.get(1), layer2) >= 0) && 
						(static_identifiers.get(2).size() == all_layers.get(2).size() || Collections.binarySearch(static_identifiers.get(2), layer3) >= 0) && 
						(static_identifiers.get(3).size() == all_layers.get(3).size() || Collections.binarySearch(static_identifiers.get(3), layer4) >= 0) && 
						(static_identifiers.get(4).size() == all_layers.get(4).size() || Collections.binarySearch(static_identifiers.get(4), layer5) >= 0) )	
				{
					
					for (String s5_s5r_a : conversion_and_rotation.get(1)) {	// this list is covertype_conversions_and_regeneration_rotation_ages_list);
						if (!ea_conversion_and_rotation_for_strata_without_sizeclass.get(strata_5layers_id).contains(s5_s5r_a)) {
							ea_conversion_and_rotation_for_strata_without_sizeclass.get(strata_5layers_id).add(s5_s5r_a);
						}
					}
				}
				
//				// Without RRB9 --> no need all_layers
//				if (	Collections.binarySearch(static_identifiers.get(0), layer1) >= 0 && 
//						Collections.binarySearch(static_identifiers.get(1), layer2) >= 0 && 
//						Collections.binarySearch(static_identifiers.get(2), layer3) >= 0 && 
//						Collections.binarySearch(static_identifiers.get(3), layer4) >= 0 && 
//						Collections.binarySearch(static_identifiers.get(4), layer5) >= 0	)	
//				{
//					for (String s5_s5r_a : conversion_and_rotation.get(1)) {	// this list is covertype_conversions_and_regeneration_rotation_ages_list);
//						if (!ea_conversion_and_rotation_for_strata_without_sizeclass.get(strata_5layers_id).contains(s5_s5r_a)) {
//							ea_conversion_and_rotation_for_strata_without_sizeclass.get(strata_5layers_id).add(s5_s5r_a);
//						}
//					}
//				}
			}
		}	
		
		for (List<String> i : ea_conversion_and_rotation_for_strata_without_sizeclass) {
			Collections.sort(i);	// sort to use Binary Search when needed (in Panel_Solve)
		}
	}	
	
	public int get_ea_totalRows() {
		return ea_totalRows;
	}
	
	public List<List<String>> get_ea_conversion_and_rotation_for_strata() {
		return ea_conversion_and_rotation_for_strata;		// each existing stratum includes a list of "layer5 layer5_regen rotation_age"
	}
	
	public List<List<String>> get_ea_conversion_and_rotation_for_strata_without_sizeclass() {
		return ea_conversion_and_rotation_for_strata_without_sizeclass;		// each regenerated stratum includes a list of "layer5 layer5_regen rotation_age"
	}	
	
	//-------------------------------------------------------------------------------------------------------------------------------------------------	
	//For input_05_non_sr_disturbances 
	private int non_sr_totalRows, non_sr_totalColumns;
	private String[][] non_sr_value;
	private String[] percentage_MS_E_for_strata;
	private String[] percentage_BS_E_for_strata;

	public void read_non_sr_disturbaces(File file) {
		String delimited = "\t";		// tab delimited
				
		try {		
			// All lines to be in array
			List<String> list;
			list = Files.readAllLines(Paths.get(file.getAbsolutePath()), StandardCharsets.UTF_8);
			list.remove(0);	// Remove the first row (Column names)
			String[] a = list.toArray(new String[list.size()]);
								
			non_sr_totalRows = a.length;
			non_sr_totalColumns = a[0].split(delimited).length;		// a[0].split(delimited) = String[] of the first row (this is the row below the column headers row which was removed already)	
			non_sr_value = new String[non_sr_totalRows][non_sr_totalColumns];
		
			// read all values from all rows and columns
			for (int i = 0; i < non_sr_totalRows; i++) {		
				String[] rowValue = a[i].split(delimited);		
				for (int j = 0; j < non_sr_totalColumns; j++) {
					non_sr_value[i][j] = rowValue[j];
				}
			}
		} catch (IOException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}
	
	public List<List<String>> get_non_sr_static_identifiers_in_row(int row) {
		List<List<String>> non_sr_static_identifiers = new ArrayList<List<String>>();
		
		//Read the whole cell into array
		String[] staticLayer_Info = non_sr_value[row][2].split(";");
		int total_staticIdentifiers = staticLayer_Info.length;
		
		//Get all static Identifiers to be in the list
		for (int i = 0; i < total_staticIdentifiers; i++) {		//6 first identifiers is strata 6 layers (layer 0 to 5)		
			List<String> thisIdentifier = new ArrayList<String>();
			
			String[] identifierElements = staticLayer_Info[i].split("\\s+");				//space delimited
			for (int j = 1; j < identifierElements.length; j++) {		//Ignore the first element which is the identifier index, so we loop from 1 not 0
				thisIdentifier.add(identifierElements[j].replaceAll("\\s+",""));		//Add element name, if name has spaces then remove all the spaces
			}
			
			non_sr_static_identifiers.add(thisIdentifier);
		}
			
		return non_sr_static_identifiers;
	}	
	
	public String get_MS_E_percentage_in_row(int row) {
		return non_sr_value[row][3];
	}
	
	public String get_BS_E_percentage_in_row(int row) {
		return non_sr_value[row][4];
	}
			
	public void populate_non_sr_lists(List<String> model_strata, List<String> model_strata_without_sizeclass, List<List<String>> all_layers) {	
		// Calculate this first to avoid calculating it in the below loops------------------- ------------------------------------
		List<List<String>>[] non_sr_static_identifiers = new ArrayList[non_sr_totalRows]; 
		for (int row = 0; row < non_sr_totalRows; row++) {	// load each row
			non_sr_static_identifiers[row] = get_non_sr_static_identifiers_in_row(row);			
		}
		
		
		// existing 6 layers ------------------------------------ ------------------------------------ ------------------------------------
		percentage_MS_E_for_strata = new String[model_strata.size()];
		percentage_BS_E_for_strata = new String[model_strata.size()];
		
		
		for (int strata_id = 0; strata_id < model_strata.size(); strata_id++) {
			String strata = model_strata.get(strata_id);
			String layer1 = strata.substring(0, 1);
			String layer2 = strata.substring(1, 2);
			String layer3 = strata.substring(2, 3);
			String layer4 = strata.substring(3, 4);
			String layer5 = strata.substring(4, 5);
			String layer6 = strata.substring(5, 6);
		
			for (int row = 0; row < non_sr_totalRows; row++) {	// load each row (or each priority condition)
				List<List<String>> static_identifiers = non_sr_static_identifiers[row];			
				String ms_e_percentage = get_MS_E_percentage_in_row(row);	
				String bs_e_percentage = get_BS_E_percentage_in_row(row);
				
				// The below check also implements Speed Boost RRB9
				if (	(static_identifiers.get(0).size() == all_layers.get(0).size() || Collections.binarySearch(static_identifiers.get(0), layer1) >= 0) && 
						(static_identifiers.get(1).size() == all_layers.get(1).size() || Collections.binarySearch(static_identifiers.get(1), layer2) >= 0) && 
						(static_identifiers.get(2).size() == all_layers.get(2).size() || Collections.binarySearch(static_identifiers.get(2), layer3) >= 0) && 
						(static_identifiers.get(3).size() == all_layers.get(3).size() || Collections.binarySearch(static_identifiers.get(3), layer4) >= 0) && 
						(static_identifiers.get(4).size() == all_layers.get(4).size() || Collections.binarySearch(static_identifiers.get(4), layer5) >= 0) && 
						(static_identifiers.get(5).size() == all_layers.get(5).size() || Collections.binarySearch(static_identifiers.get(5), layer6) >= 0)	)	
				{
					if (percentage_MS_E_for_strata[strata_id] == null || percentage_MS_E_for_strata[strata_id].equals("null")) percentage_MS_E_for_strata[strata_id] = ms_e_percentage;
					if (percentage_BS_E_for_strata[strata_id] == null || percentage_BS_E_for_strata[strata_id].equals("null")) percentage_BS_E_for_strata[strata_id] = bs_e_percentage;
				}
			}
			
			// There might be the case this stratum is not captured in any conditions. In this case we make them the percentage to be zero
			if (percentage_MS_E_for_strata[strata_id] == null) percentage_MS_E_for_strata[strata_id] = "0.0";		// no MS_E on this stratum
			if (percentage_BS_E_for_strata[strata_id] == null) percentage_BS_E_for_strata[strata_id] = "0.0";		// no BS_E on this stratum
		}
	}	
	
	public double[] get_percentage_MS_E_for_strata() {
		double[] percentage_MS_E = new double[percentage_MS_E_for_strata.length];
		for (int i = 0; i < percentage_MS_E.length; i++) {
			String value = percentage_MS_E_for_strata[i];
			percentage_MS_E[i] = (!value.equals("null")) ? Double.parseDouble(value) : (double) -9999;	// null --> -9999 indicate the bounds for MS_E should be turned off
		}
		return percentage_MS_E;
	}
	
	public double[]  get_percentage_BS_E_for_strata() {
		double[] percentage_BS_E = new double[percentage_BS_E_for_strata.length];
		for (int i = 0; i < percentage_BS_E.length; i++) {
			String value = percentage_BS_E_for_strata[i];
			percentage_BS_E[i] = (!value.equals("null")) ? Double.parseDouble(value) : (double) -9999;	// null --> -9999 indicate the bounds for BS_E should be turned off
		}
		return percentage_BS_E;
	}
	
	//-------------------------------------------------------------------------------------------------------------------------------------------------	
	//For input_06_sr_disturbances
	private List<String> disturbance_condition_list;
	
	public void read_replacing_disturbances(File file) {
		try {
			// All lines except the 1st line to be in a list;		
			disturbance_condition_list = Files.readAllLines(Paths.get(file.getAbsolutePath()), StandardCharsets.UTF_8);
			disturbance_condition_list.remove(0);	// Remove the first row (Column names)

		} catch (IOException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}		
							
	public List<String> get_disturbance_condition_list() {
		return disturbance_condition_list;
	}			
				
	//-------------------------------------------------------------------------------------------------------------------------------------------------	
	//For input_07_management_cost
	private List<String> cost_condition_list;
	
	public void read_management_cost(File file) {
		try {
			// All lines except the 1st line to be in a list;		
			cost_condition_list = Files.readAllLines(Paths.get(file.getAbsolutePath()), StandardCharsets.UTF_8);
			cost_condition_list.remove(0);	// Remove the first row (Column names)

		} catch (IOException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}		
	
	public List<String> get_cost_condition_list() {
		return cost_condition_list;
	}
			
	
	//-------------------------------------------------------------------------------------------------------------------------------------------------
	//For input_08_basic_constraints
	private List<String> constraint_column_names_list;
	private int bc_total_rows, bc_total_columns;
	private String[][] bc_values;
	private int constraint_id_col, constraint_description_col, constraint_type_col, constraint_multiplier_col, lowerbound_col, lowerbound_perunit_penalty_col,
			upperbound_col, upperbound_perunit_penalty_col, parameter_index_col, static_identifiers_col, dynamic_identifiers_col;

	public void read_basic_constraints(File file) {
		String delimited = "\t";		// tab delimited
				
		try {		
			// All lines to be in array
			List<String> list;
			list = Files.readAllLines(Paths.get(file.getAbsolutePath()), StandardCharsets.UTF_8);
			List<String> remove_list = new ArrayList<String>();	// this list contains all lines which have bc_type = IDLE
			for (String i : list) {
				if (i.split(delimited)[2].equals("IDLE")) {
					remove_list.add(i);
				}
			}
			list.removeAll(remove_list);	// remove bc_type = IDLE lines from the list
			String[] a = list.toArray(new String[list.size()]);
						
			//Read the first row into array. This will be Column names
			String[] columnName = a[0].split(delimited);
			
			//List of constraint column names
			constraint_column_names_list = Arrays.asList(columnName);	
			constraint_id_col = constraint_column_names_list.indexOf("bc_id");
			constraint_description_col = constraint_column_names_list.indexOf("bc_description");
			constraint_type_col = constraint_column_names_list.indexOf("bc_type");
			constraint_multiplier_col = constraint_column_names_list.indexOf("bc_multiplier");
			lowerbound_col = constraint_column_names_list.indexOf("lowerbound");
			lowerbound_perunit_penalty_col = constraint_column_names_list.indexOf("lowerbound_perunit_penalty");
			upperbound_col = constraint_column_names_list.indexOf("upperbound");
			upperbound_perunit_penalty_col = constraint_column_names_list.indexOf("upperbound_perunit_penalty");
			parameter_index_col = constraint_column_names_list.indexOf("parameter_index");
			static_identifiers_col = constraint_column_names_list.indexOf("static_identifiers");
			dynamic_identifiers_col = constraint_column_names_list.indexOf("dynamic_identifiers");	
			
			//Values in all rows and columns
			bc_total_rows = a.length;
			bc_total_columns = columnName.length;				
			bc_values = new String[bc_total_rows][bc_total_columns];
		
			// read all values from all rows and columns
			for (int i = 1; i < bc_total_rows; i++) {		//From 2nd row			
				String[] rowValue = a[i].split(delimited);		
				for (int j = 0; j < bc_total_columns; j++) {
					bc_values[i][j] = rowValue[j];
				}
			}

		} catch (IOException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}	

	public List<String> get_constraint_column_names_list() {
		return constraint_column_names_list;
	}

	public String[][] get_bc_values() {
		return bc_values;
	}

	public int get_UC_TotalRows() {
		return bc_total_rows;
	}

	public int get_UC_TotalColumns() {
		return bc_total_columns;
	}
		
	public int get_total_hardConstraints() {
		int total = 0;
		for (int i = 1; i < bc_total_rows; i++) {		//From 2nd row			
			if (bc_values[i][constraint_type_col].equals("HARD")) total++;
		}	
		return total;
	}	

	public double[] get_hardConstraints_LB() {
		List<Double> list = new ArrayList<Double>();
		for (int i = 1; i < bc_total_rows; i++) {		//From 2nd row			
			if (bc_values[i][constraint_type_col].equals("HARD") && !bc_values[i][lowerbound_col].equals("null")) list.add(Double.parseDouble(bc_values[i][lowerbound_col]));
			if (bc_values[i][constraint_type_col].equals("HARD") && bc_values[i][lowerbound_col].equals("null")) list.add((double) 0);		//Change "null" value of LB to 0
		}			
		double[] array = Stream.of(list.toArray(new Double[list.size()])).mapToDouble(Double::doubleValue).toArray();
		return array;
	}

	public double[] get_hardConstraints_UB() {
		List<Double> list = new ArrayList<Double>();
		for (int i = 1; i < bc_total_rows; i++) {		//From 2nd row			
			if (bc_values[i][constraint_type_col].equals("HARD")  && !bc_values[i][upperbound_col].equals("null")) list.add(Double.parseDouble(bc_values[i][upperbound_col]));
			if (bc_values[i][constraint_type_col].equals("HARD")  && bc_values[i][upperbound_col].equals("null")) list.add(Double.MAX_VALUE);	//Change "null" value of UB to Double.Max_Value
		}			
		double[] array = Stream.of(list.toArray(new Double[list.size()])).mapToDouble(Double::doubleValue).toArray();
		return array;
	}
	
	public int get_total_softConstraints() {
		int total = 0;	
		for (int i = 1; i < bc_total_rows; i++) {		//From 2nd row			
			if (bc_values[i][constraint_type_col].equals("SOFT")) total++;
		}	
		return total;
	}		
				
	
	public double[] get_softConstraints_LB() {
		List<Double> list = new ArrayList<Double>();
		for (int i = 1; i < bc_total_rows; i++) {		//From 2nd row			
			if (bc_values[i][constraint_type_col].equals("SOFT") && !bc_values[i][lowerbound_col].equals("null")) list.add(Double.parseDouble(bc_values[i][lowerbound_col]));
			if (bc_values[i][constraint_type_col].equals("SOFT") && bc_values[i][lowerbound_col].equals("null")) list.add((double) 0);		//Change "null" value of LB to 0
		}				
		double[] array = Stream.of(list.toArray(new Double[list.size()])).mapToDouble(Double::doubleValue).toArray();
		return array;
	}

	public double[] get_softConstraints_UB() {	
		List<Double> list = new ArrayList<Double>();
		for (int i = 1; i < bc_total_rows; i++) {		//From 2nd row			
			if (bc_values[i][constraint_type_col].equals("SOFT")  && !bc_values[i][upperbound_col].equals("null")) list.add(Double.parseDouble(bc_values[i][upperbound_col]));
			if (bc_values[i][constraint_type_col].equals("SOFT")  && bc_values[i][upperbound_col].equals("null")) list.add(Double.MAX_VALUE);	//Change "null" value of UB to Double.Max_Value
		}			
		double[] array = Stream.of(list.toArray(new Double[list.size()])).mapToDouble(Double::doubleValue).toArray();
		return array;
	}	
	
	public double[] get_softConstraints_LB_Weight() {
		List<Double> list = new ArrayList<Double>();
		for (int i = 1; i < bc_total_rows; i++) {		//From 2nd row			
			if (bc_values[i][constraint_type_col].equals("SOFT") && !bc_values[i][lowerbound_perunit_penalty_col].equals("null")) list.add(Double.parseDouble(bc_values[i][lowerbound_perunit_penalty_col]));
			if (bc_values[i][constraint_type_col].equals("SOFT") && bc_values[i][lowerbound_perunit_penalty_col].equals("null")) list.add((double) 0);		//Change "null" value of LB_Weight to 0
		}			
		double[] array = Stream.of(list.toArray(new Double[list.size()])).mapToDouble(Double::doubleValue).toArray();
		return array;
	}

	public double[] get_softConstraints_UB_Weight() {	
		List<Double> list = new ArrayList<Double>();
		for (int i = 1; i < bc_total_rows; i++) {		//From 2nd row			
			if (bc_values[i][constraint_type_col].equals("SOFT") && !bc_values[i][upperbound_perunit_penalty_col].equals("null")) list.add(Double.parseDouble(bc_values[i][upperbound_perunit_penalty_col]));
			if (bc_values[i][constraint_type_col].equals("SOFT") && bc_values[i][upperbound_perunit_penalty_col].equals("null")) list.add((double) 0);		//Change "null" value of UB_Weight to 0
		}			
		double[] array = Stream.of(list.toArray(new Double[list.size()])).mapToDouble(Double::doubleValue).toArray();
		return array;
	}		

	
	public int get_total_freeConstraints() {
		int total = 0;
		for (int i = 1; i < bc_total_rows; i++) {		//From 2nd row		
			if (bc_values[i][constraint_type_col].equals("FREE")) total++;
		}	
		return total;
	}	
	
	
	public List<List<String>> get_static_identifiers_in_row(int row) {
		List<List<String>> static_identifiers = new ArrayList<List<String>>();
		
		//Read the whole cell into array
		String[] staticLayer_Info = bc_values[row][static_identifiers_col].split(";");		//Note: row 0 is the title only, row 1 is constraint 1,.....
		int total_staticIdentifiers = staticLayer_Info.length;
		
		//Get all static Identifiers to be in the list
		for (int i = 0; i < total_staticIdentifiers; i++) {		//6 first identifiers is strata 6 layers (layer 0 to 5)		
			List<String> thisIdentifier = new ArrayList<String>();
			
			String[] identifierElements = staticLayer_Info[i].split("\\s+");				//space delimited
			for (int j = 1; j < identifierElements.length; j++) {		//Ignore the first element which is the identifier index, so we loop from 1 not 0
				thisIdentifier.add(identifierElements[j].replaceAll("\\s+",""));		//Add element name, if name has spaces then remove all the spaces
			}
			
			static_identifiers.add(thisIdentifier);
		}
			
		return static_identifiers;
	}	
	
	
	public Set<String> get_static_strata(int row) {
		Set<String> static_strata = new HashSet<String>();
		List<List<String>> static_identifiers = get_static_identifiers_in_row(row);
		// first 6 layers
		for (String layer1: static_identifiers.get(0)) {
			for (String layer2: static_identifiers.get(1)) {
				for (String layer3: static_identifiers.get(2)) {
					for (String layer4: static_identifiers.get(3)) {
						for (String layer5: static_identifiers.get(4)) {
							for (String layer6: static_identifiers.get(5)) {
								StringBuilder combined_name = new StringBuilder();
								combined_name = combined_name.append(layer1);
								combined_name = combined_name.append(layer2);
								combined_name = combined_name.append(layer3);
								combined_name = combined_name.append(layer4);
								combined_name = combined_name.append(layer5);
								combined_name = combined_name.append(layer6);
								static_strata.add(combined_name.toString());
							}							
						}						
					}					
				}				
			}	
		}
		return static_strata;
	}	

	
	public Set<String> get_static_strata_without_sizeclass(int row) {	
		Set<String> static_strata_without_sizeclass = new HashSet<String>();
		List<List<String>> static_identifiers = get_static_identifiers_in_row(row);
		// first 5 layers
		for (String layer1: static_identifiers.get(0)) {
			for (String layer2: static_identifiers.get(1)) {
				for (String layer3: static_identifiers.get(2)) {
					for (String layer4: static_identifiers.get(3)) {
						for (String layer5: static_identifiers.get(4)) {
							StringBuilder combined_name = new StringBuilder();
							combined_name = combined_name.append(layer1);
							combined_name = combined_name.append(layer2);
							combined_name = combined_name.append(layer3);
							combined_name = combined_name.append(layer4);
							combined_name = combined_name.append(layer5);
							static_strata_without_sizeclass.add(combined_name.toString());
						}					
					}					
				}				
			}	
		}
		return static_strata_without_sizeclass;
	}
	
	
	public Set<String> get_static_strata_without_sizeclass_and_covertype(int row) {	
		Set<String> static_strata_without_sizeclass_and_covertype = new HashSet<String>();
		List<List<String>> static_identifiers = get_static_identifiers_in_row(row);
		// first 4 layers
		for (String layer1: static_identifiers.get(0)) {
			for (String layer2: static_identifiers.get(1)) {
				for (String layer3: static_identifiers.get(2)) {
					for (String layer4: static_identifiers.get(3)) {
						StringBuilder combined_name = new StringBuilder();
						combined_name = combined_name.append(layer1);
						combined_name = combined_name.append(layer2);
						combined_name = combined_name.append(layer3);
						combined_name = combined_name.append(layer4);
						static_strata_without_sizeclass_and_covertype.add(combined_name.toString());
					}					
				}				
			}	
		}
		return static_strata_without_sizeclass_and_covertype;
	}	
	
	
	
	public List<String> get_static_methods(int row) {
		List<List<String>> static_identifiers = get_static_identifiers_in_row(row);
		return static_identifiers.get(6);
	}
	
	public List<String> get_static_periods(int row) {
		List<List<String>> static_identifiers = get_static_identifiers_in_row(row);	
		return static_identifiers.get(7);
	}	

	
	
	public List<List<String>> get_dynamic_identifiers_in_row(int row) {
		List<List<String>> dynamic_identifiers = new ArrayList<List<String>>();
		
		//Read the whole cell into array
		String[] dynamicLayer_Info = bc_values[row][dynamic_identifiers_col].split(";");		//Note: row 0 is the title only, row 1 is constraint 1,.....
		int total_dynamicIdentifiers = dynamicLayer_Info.length;
	
		
		//Get all dynamic Identifiers to be in the list
		for (int i = 0; i < total_dynamicIdentifiers; i++) {	
			List<String> thisIdentifier = new ArrayList<String>();
			
			String[] identifierElements = dynamicLayer_Info[i].split("\\s+");				//space delimited
			for (int j = 1; j < identifierElements.length; j++) {		//Ignore the first element which is the identifier column index, so we loop from 1 not 0
				thisIdentifier.add(identifierElements[j].replaceAll("\\s+",""));		//Add element name, if name has spaces then remove all the spaces
			}
			
			dynamic_identifiers.add(thisIdentifier);
		}
			
		return dynamic_identifiers;
	}	
	
	
	public List<String> get_dynamic_identifiers_column_indexes_in_row(int row) {	//Column 8 in the GUI table "Dynamic identifiers". The whole is contained by UC_value[i][8]
		List<String> dynamic_identifiers_column_indexes = new ArrayList<String>();
			
		//Read the whole cell into array
		String[] dynamicLayer_Info = bc_values[row][dynamic_identifiers_col].split(";");		//Note: row 0 is the title only, row 1 is constraint 1,.....
		int total_dynamicIdentifiers = dynamicLayer_Info.length;

		//Get all dynamic Identifiers to be in the list
		for (int i = 0; i < total_dynamicIdentifiers; i++) {	
			String[] identifierElements = dynamicLayer_Info[i].split("\\s+");				//space delimited
			//add the first element which is the identifier column index
			dynamic_identifiers_column_indexes.add(identifierElements[0].replaceAll("\\s+",""));
		}
			
		return dynamic_identifiers_column_indexes;
	}	
	
	
	public List<String> get_parameters_indexes(int row) {	
		List<String> parameters_indexes_list = new ArrayList<String>();
		
		//Read the whole cell into array
		String[] parameter_Info = bc_values[row][parameter_index_col].split("\\s+");			
		for (int i = 0; i < parameter_Info.length; i++) {	
			parameters_indexes_list.add(parameter_Info[i].replaceAll("\\s+",""));
		}				
		return parameters_indexes_list;
	}	
	
	//-------------------------------------------------------------------------------------------------------------------------------------------------
	//For input_09_flow_constraints
	private List<String> flow_column_names_list;
	private int flow_totalRows, flow_totalColumns;
	private String[][] flow_value;
	private int flow_id_col, flow_description_col, flow_arrangement_col, flow_type_col, lowerbound_percentage_col, upperbound_percentage_col;		

	public void read_flow_constraints(File file) {
		String delimited = "\t";		// tab delimited
				
		try {		
			// all lines to be in array
			List<String> list;
			list = Files.readAllLines(Paths.get(file.getAbsolutePath()), StandardCharsets.UTF_8);
			String[] a = list.toArray(new String[list.size()]);
						
			// read the first row into array. This will be column names
			String[] columnName = a[0].split(delimited);
			
			// list of constraint column names
			flow_column_names_list = Arrays.asList(columnName);	
			flow_id_col = flow_column_names_list.indexOf("flow_id");
			flow_description_col = flow_column_names_list.indexOf("flow_description");
			flow_arrangement_col = flow_column_names_list.indexOf("flow_arrangement");
			flow_type_col = flow_column_names_list.indexOf("flow_type");
			lowerbound_percentage_col = flow_column_names_list.indexOf("lowerbound_percentage");
			upperbound_percentage_col = flow_column_names_list.indexOf("upperbound_percentage");

			
			// values in all rows and columns
			flow_totalRows = a.length;
			flow_totalColumns = columnName.length;				
			flow_value = new String[flow_totalRows][flow_totalColumns];
		
			// read all values from all rows and columns
			for (int i = 1; i < flow_totalRows; i++) {		// from 2nd row			
				String[] rowValue = a[i].split(delimited);		
				for (int j = 0; j < flow_totalColumns; j++) {
					flow_value[i][j] = rowValue[j];
				}
			}
			
		} catch (IOException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}				
	
	public List<String> get_flow_column_names_list() {
		return flow_column_names_list;
	}
	
	public String[][] get_flow_values() {
		return flow_value;
	}

	public int get_flow_TotalRows() {
		return flow_totalRows;
	}

	public int get_flow_TotalColumns() {
		return flow_totalColumns;
	}
		
	public List<Integer> get_flow_id_list() {
		List<Integer> get_flow_id_list = new ArrayList<Integer>();
		for (int i = 1; i < flow_totalRows; i++) {		// from 2nd row			
			get_flow_id_list.add(Integer.parseInt(flow_value[i][flow_id_col]));
		}
		return get_flow_id_list;
	}
	
	public List<String> get_flow_description_list() {
		List<String> flow_description_list = new ArrayList<String>();	
		for (int i = 1; i < flow_totalRows; i++) {		// from 2nd row			
			flow_description_list.add(flow_value[i][flow_description_col]);
		}
		return flow_description_list;
	}
	
	public List<String> get_flow_arrangement_list() {
		List<String> flow_arrangement_list = new ArrayList<String>();	
		for (int i = 1; i < flow_totalRows; i++) {		// from 2nd row			
			flow_arrangement_list.add(flow_value[i][flow_arrangement_col]);
		}
		return flow_arrangement_list;
	}
	
	public List<String> get_flow_type_list() {
		List<String> flow_type_list = new ArrayList<String>();	
		for (int i = 1; i < flow_totalRows; i++) {		// from 2nd row			
			flow_type_list.add(flow_value[i][flow_type_col]);
		}
		return flow_type_list;
	}
	
	public List<Double> get_flow_lowerbound_percentage_list() {
		List<Double> flow_lowerbound_percentage_list = new ArrayList<Double>();
		for (int i = 1; i < flow_totalRows; i++) {		// from 2nd row			
			if (!flow_value[i][lowerbound_percentage_col].equals("null")) {
				flow_lowerbound_percentage_list.add(Double.parseDouble(flow_value[i][lowerbound_percentage_col]));
			} else {
				flow_lowerbound_percentage_list.add(null);
			}
		}
		return flow_lowerbound_percentage_list;
	}
	
	public List<Double> get_flow_upperbound_percentage_list() {
		List<Double> flow_upperbound_percentage_list = new ArrayList<Double>();
		for (int i = 1; i < flow_totalRows; i++) {		// from 2nd row						
			if (!flow_value[i][upperbound_percentage_col].equals("null")) {
				flow_upperbound_percentage_list.add(Double.parseDouble(flow_value[i][upperbound_percentage_col]));
			} else {
				flow_upperbound_percentage_list.add(null);
			}
		}
		return flow_upperbound_percentage_list;
	}	
	
	public List<List<List<Integer>>> get_flow_set_list() {
		List<List<List<Integer>>> flow_set_list = new ArrayList<List<List<Integer>>>();	
		for (int i = 1; i < flow_totalRows; i++) {		// from 2nd row			
			List<List<Integer>> this_set = new ArrayList<List<Integer>>();				
			String[] flow_arrangement_info = flow_value[i][flow_arrangement_col].split(";");	// Read the whole cell 'flow_arrangement'
			int this_set_size = flow_arrangement_info.length;
			
			for (int j = 0; j < this_set_size; j++) {
				List<Integer> this_term = new ArrayList<Integer>();
				String[] this_term_info = flow_arrangement_info[j].split("\\s+");		// Read each term in this arrangement
				int this_term_size = this_term_info.length;	
								
				for (int k = 0; k < this_term_size; k++) {
					this_term.add(Integer.parseInt(this_term_info[k]));										
				}
				this_set.add(this_term);
			}
			flow_set_list.add(this_set);
		}
		return flow_set_list;
	}	
}
