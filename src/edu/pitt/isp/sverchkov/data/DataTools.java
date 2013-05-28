/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.isp.sverchkov.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 * @author YUS24
 */
public class DataTools {
    
    public final static String DELIMITER = " *, *";
            
    public static DataTable<String,String> dataTableFromFile( File file ) throws FileNotFoundException{
        DataTable<String,String> data = null;
        try ( Scanner in = new Scanner( file ) ){
            data = new DataTableImpl<>( Arrays.asList( in.nextLine().trim().split(DELIMITER) ) );
            while( in.hasNextLine() )
                data.addRow( Arrays.asList( in.nextLine().trim().split(DELIMITER) ) );
        }
        return data;
    }
}
