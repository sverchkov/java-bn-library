/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.isp.sverchkov.exec;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author YUS24
 */
public class ArgParser {
    
    private final String[] args;
    private final Map<String,Hook> hooks = new HashMap<>();
    
    public ArgParser( String... args ){
        this.args = args;
    }
    
    public void fill(){
        for( int i=0; i<args.length; i++ ){
            Hook hook = hooks.get( args[i] );
            if( null != hook ) i = hook.run( i );
            else warn(i);
        }
    }
    
    private void warn( int index ){
        System.err.println("Warning: unrecognized command line argument: "+args[index]+".");
    }
    
    public Parcel<String> string( String arg, String defaultValue ){
        StringParcel parcel = new StringParcel( defaultValue );
        hooks.put( arg, parcel );
        return parcel;
    }
    
    public Parcel<String> string( String arg ){
        return string( arg, null );
    }
    
    public Parcel<File> file( String arg, File defaultValue ){
        FileParcel parcel = new FileParcel( defaultValue );
        hooks.put( arg, parcel );
        return parcel;
    }
    
    public Parcel<File> file( String arg ){
        return file( arg, null );
    }
    
    public Parcel<Double> adouble( String arg, double defaultValue ){
        DoubleParcel parcel = new DoubleParcel( defaultValue );
        hooks.put( arg, parcel );
        return parcel;
    }
    
    public Parcel<Double> adouble( String arg ){
        return adouble( arg, Double.NaN );
    }
    
    public Parcel<Integer> integer( String arg, Integer defaultValue ){
        IntParcel parcel = new IntParcel( defaultValue );
        hooks.put( arg, parcel );
        return parcel;
    }
    
    public Parcel<Boolean> flag( String arg ){
        BoolParcel parcel = new BoolParcel();
        hooks.put( arg, parcel );
        return parcel;
    }
    
    public <E extends Enum<E>> Parcel<E> anenum( String arg, Class<E> eClass, E defaultValue ){
        EnumParcel<E> parcel = new EnumParcel<>( eClass, defaultValue );
        hooks.put(arg, parcel);
        return parcel;
    }
    
    private class BoolParcel implements Parcel<Boolean>, Hook {
        private Boolean thing;
        BoolParcel(){
            thing = Boolean.FALSE;
        }

        @Override
        public Boolean get() {
            return thing;
        }

        @Override
        public int run(int index) {
            thing = Boolean.TRUE;
            return index;        }
        
    }
    
    private class FileParcel implements Parcel<File>, Hook {
        private File thing;
        FileParcel( File defaultValue ){ thing = defaultValue; }
        @Override
        public int run(int index) {
            thing = new File( args[++index] );
            return index;
        }
        @Override
        public File get() {
            return thing;
        }
    }
    
    private class IntParcel implements Parcel<Integer>, Hook {
        private Integer thing;
        public IntParcel( Integer i ){
            thing = i;
        }
        @Override
        public int run(int index) {
            thing = new Integer( args[++index] );
            return index;
        }
        @Override
        public Integer get() {
            return thing;
        }
    }
    
    private class DoubleParcel implements Parcel<Double>, Hook {
        private Double thing;
        
        public DoubleParcel( double d ){
            thing = d;
        }

        @Override
        public Double get() {
            return thing;
        }

        @Override
        public int run(int index) {
            thing = new Double( args[++index] );
            return index;
        }        
    }
    
    private class StringParcel implements Parcel<String>, Hook {
        private String thing;
        public StringParcel( String defaultValue ){
            thing = defaultValue;
        }
        @Override
        public int run(int index) {
            thing = args[++index];
            return index;
        }
        @Override
        public String get() {
            return thing;
        }
    }
    
    private class EnumParcel<E extends Enum<E>> implements Parcel<E>, Hook {
        private E thing;
        private final Class<E> eClass;

        public EnumParcel( Class<E> clazz, E defaultValue ){
            eClass = clazz;
            thing = defaultValue;
        }
        
        @Override
        public E get() {
            return thing;
        }

        @Override
        public int run(int index) {
            thing = Enum.valueOf( eClass, args[++index] );
            return index;
        }
        
    }
    
    public interface Parcel<Thing> {
        Thing get();
    }
        
    private interface Hook {
        /**
         * @param index the current index in reading the args array
         * @return the index of the last element read by the hook
         */
        int run( int index );
    }
}
