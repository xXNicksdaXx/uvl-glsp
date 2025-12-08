package de.tu_dresden.inf.st.uvl.glsp.model;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.tu_dresden.inf.st.uvl.glsp.notation.NotationData;
import de.vill.model.FeatureModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.glsp.server.model.DefaultGModelState;
import org.eclipse.glsp.server.session.ClientSession;
import org.eclipse.glsp.server.session.ClientSessionListener;
import org.eclipse.glsp.server.session.ClientSessionManager;

@Singleton
public class UVLModelStateImpl extends DefaultGModelState implements UVLModelState, ClientSessionListener {

    protected static Logger LOGGER = LogManager.getLogger(UVLModelStateImpl.class.getSimpleName());

    @Inject
    protected ClientSessionManager clientSessionManager;

    protected FeatureModel featureModel;

    protected NotationData notationData;

    @Override
    @Inject
    public void init() {
        this.clientSessionManager.addListener(this, this.clientId);
    }

    @Override
    public FeatureModel getUVLModel() {
        return this.featureModel;
    }

    @Override
    public void setUVLModel(FeatureModel model) {
        this.featureModel = model;
        // setCommandStack(this.featureModel.getCommandStack());
    }

    @Override
    public NotationData getNotationData() {
        return this.notationData;
    }

    @Override
    public void setNotationData(NotationData notationData) {
        this.notationData = notationData;
    }

    @Override
    public void sessionDisposed(final ClientSession clientSession) {
        this.index.clear();
    }
}
