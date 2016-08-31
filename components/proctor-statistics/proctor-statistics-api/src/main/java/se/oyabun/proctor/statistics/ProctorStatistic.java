package se.oyabun.proctor.statistics;

/**
 * Proctor Statistic Types
 */
public enum ProctorStatistic {

    PROXY_REQUEST_RECEIVED,
    PROXY_REPLY_SENT,
    PROXY_HANDLER_MATCH,
    PROXY_HANDLER_MISS;

    public static boolean matchesAny(String value) {

        for(ProctorStatistic proctorStatistic : ProctorStatistic.values()) {

            if(proctorStatistic.name().equals(value)) {

                return true;

            }

        }

        return false;

    }

}
