package se.oyabun.proctor.events.cluster;

import se.oyabun.proctor.events.ProctorApplicationEvent;

public class ClusterNodeAddedEvent<ProctorServerConfiguration>
        extends ProctorApplicationEvent {

    public ClusterNodeAddedEvent(ProctorServerConfiguration source) {

        super(source);

    }

}
