package se.oyabun.proctor.events.cluster;

import se.oyabun.proctor.events.ProctorApplicationEvent;

public class ClusterNodeRemovedEvent<ProctorServerConfiguration>
        extends ProctorApplicationEvent {

    public ClusterNodeRemovedEvent(ProctorServerConfiguration source) {

        super(source);

    }

}
