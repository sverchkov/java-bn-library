/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.isp.sverchkov.data;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author YUS24
 */
public class DataTools {
    
    public final static String NEWLINE = System.getProperty("line.separator");
    public final static String DELIMITER_REGEX = " *, *";
    public final static String DELIMITER = ", ";
            
    public static DataTable<String,String> dataTableFromFile( File file ) throws FileNotFoundException{
        DataTable<String,String> data = null;
        try ( Scanner in = new Scanner( file ) ){
            data = new DataTableImpl<>( Arrays.asList( in.nextLine().trim().split(DELIMITER_REGEX) ) );
            while( in.hasNextLine() )
                data.addRow( Arrays.asList( in.nextLine().trim().split(DELIMITER_REGEX) ) );
        }
        return data;
    }
    
    public static <Attribute,Value> void saveCSV( DataTable<Attribute,Value> data, File dest, boolean headers ) throws IOException{
        try( BufferedWriter out = new BufferedWriter( new FileWriter( dest ) ) ){
            
            if( headers ){
                String delim = "";
                for( Attribute a : data.variables() ){
                    out.append(delim).append( a.toString() );
                    delim = DELIMITER;
                }
                out.append(NEWLINE);
            }
            
            for( List<Value> row : data ){
                String delim = "";
                for( Value v : row ){
                    out.append(delim).append( v.toString() );
                    delim = DELIMITER;
                }
                out.append(NEWLINE);
            }
        }
    }
}
