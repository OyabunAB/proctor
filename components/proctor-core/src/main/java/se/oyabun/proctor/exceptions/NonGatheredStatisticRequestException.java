package se.oyabun.proctor.exceptions;

/**
 * Thrown when a request is made for a non gathered statistic
 */
public class NonGatheredStatisticRequestException
        extends Exception {

    public NonGatheredStatisticRequestException(String message) {

        super(message);

    }

    public NonGatheredStatisticRequestException(String message, Throwable e) {

        super(message, e);

    }

}
