package be.ac.ulb.infof307.g03.viewsControllers;

/**
 * Class controlling the WelcomeView
 */
public class WelcomeViewController extends BaseViewController {


    private ViewListener listener;

    public void setListener(ViewListener listener){
        this.listener = listener;
    }

    public void onMouseClickedSignIn() {
        listener.signIn();
    }

    public void onMouseClickedSignUp(){
        listener.createConditionController();
    }


    public interface ViewListener{
        void signIn();
        void createConditionController();
    }
}
