package com.example.jasmine.progettoinfo3;

/**
 * Copied from stack overflow
 */
public interface AsyncResponse {

    /**
     * This funcion is called when the server return the result of the computation
     * @param output The result received from HTTP POST from the server
     */
    void processFinish(String output);

}
