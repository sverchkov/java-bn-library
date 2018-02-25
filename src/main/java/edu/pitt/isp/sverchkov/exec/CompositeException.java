/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.pitt.isp.sverchkov.exec;

/**
 *
 * @author YUS24
 */
public abstract class CompositeException extends RuntimeException {
    
    public static final CompositeException EMPTY = new EmptyCE();
    
    protected abstract StringBuilder fifoMessages( StringBuilder sb );
    
    public CompositeException and( Throwable t ){
        return new LinkInCE( this, t );
    }

    private CompositeException(){
        super();
    }
    
    private CompositeException(String message){
        super(message);
    }
    
    private static class EmptyCE extends CompositeException {
        @Override
        protected StringBuilder fifoMessages(StringBuilder sb) {
            return sb;
        }        
    }
    
    private static class LinkInCE extends CompositeException {
        private static final String DELIM = "|!| "; 
        private final CompositeException parent;
        private final Throwable content;
    
        public LinkInCE( CompositeException parent, Throwable exception ){
            super( parent.fifoMessages(new StringBuilder( "Multiple exceptions: " ))
                    .append(DELIM)
                    .append(exception.getLocalizedMessage())
                    .toString());
            this.parent = parent;
            this.content = exception;
        }

        @Override
        protected StringBuilder fifoMessages(StringBuilder sb) {
            return parent.fifoMessages(sb)
                    .append(DELIM)
                    .append(content.getLocalizedMessage());
        }
    }
}
