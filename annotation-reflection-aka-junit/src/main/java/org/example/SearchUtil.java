package org.example;

public class SearchUtil {

    private final String[] array;

    // private constructor
    private SearchUtil(String[] array) {
        this.array = array;
    }

    // public method
    public boolean contains(String value) {
        for(String s : array){
            if (null != s && s.equals(value)){
                return true;
            }
        }
        return false;
    }

    // static method for create builder
    public static Builder getBuilder(){
        return new Builder();
    }

    // INNER BUILDER
    public static class Builder {
        private String[] arrayForBuilder;

        // SET array
        public Builder withArray(String[] array){
            this.arrayForBuilder = array;
            return this;
        }

        // build
        public SearchUtil build(){
            return new SearchUtil(arrayForBuilder);
        }

    }

}
