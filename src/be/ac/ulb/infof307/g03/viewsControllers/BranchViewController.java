package be.ac.ulb.infof307.g03.viewsControllers;

import be.ac.ulb.infof307.g03.models.Branch;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Class controlling the BranchView
 */
public class BranchViewController extends BaseViewController implements Initializable {

    @FXML
    private TableView<Branch> branchTable;

    @FXML
    private Button returnButton;

    @FXML
    private Button addBranchButton;

    @FXML
    private TextField branchTextField;

    private ViewListener listener;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        returnButton.setOnAction(event -> listener.goBack());
        addBranchButton.setOnAction(event -> {
            if (!branchTextField.getText().equals(""))
                    listener.onAddBranch(branchTextField.getText());
                    branchTextField.setText("");
                });

        branchTable.setPlaceholder(new Label("This project does not contain any branches"));
        TableColumn<Branch, String> branchColumn = new TableColumn<>("Branch");
        branchColumn.setCellValueFactory(new PropertyValueFactory<>("branchName"));
        branchTable.getColumns().add(branchColumn);
        addButtonsToTable();
        branchTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void addButtonsToTable() {
        TableColumn<Branch, Void> colBtn = new TableColumn<>("Manage");
        Callback<TableColumn<Branch, Void>, TableCell<Branch, Void>> cellFactory = new Callback<TableColumn<Branch, Void>, TableCell<Branch, Void>>() {
            @Override
            public TableCell<Branch, Void> call(final TableColumn<Branch, Void> param) {
                return new TableCell<Branch, Void>() {
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox buttonWrapper = new HBox();
                            buttonWrapper.setSpacing(5);
                            Button removeBranchButton = new Button("Remove");
                            removeBranchButton.setOnAction((ActionEvent event) -> listener.onDeleteBranch(getTableView().getItems().get(getIndex())));
                            if(getTableView().getItems().get(getIndex()).getBranchName().equals("Master"))
                                removeBranchButton.setVisible(false);
                            Button checkoutButton = new Button("Checkout");
                            checkoutButton.setOnAction((ActionEvent event) -> listener.onCheckoutBranch(getTableView().getItems().get(getIndex())));

                            Branch branch = getTableView().getItems().get(getIndex());
                            if(listener.getCurrentBranch().getId() == branch.getId()){
                                checkoutButton.setDisable(true);
                            }


                            Button mergeBranchButton = new Button("Merge in current branch");
                            mergeBranchButton.setOnAction((ActionEvent event) -> listener.onMergeBranch(getTableView().getItems().get(getIndex())));
                            if(getTableView().getItems().get(getIndex()).getBranchName().equals("Master"))
                                mergeBranchButton.setVisible(false);
                            if(listener.getCurrentBranch().getId() == branch.getId() || branch.getVersion() == null){
                                mergeBranchButton.setDisable(true);
                            }
                            buttonWrapper.getChildren().addAll(removeBranchButton, checkoutButton,mergeBranchButton);
                            setGraphic(buttonWrapper);
                        }
                    }
                };
            }
        };
        colBtn.setCellFactory(cellFactory);
        branchTable.getColumns().add(colBtn);
    }

    public void setBranchTable(List<Branch> branches) {
        branchTable.setItems(FXCollections.observableList(branches));
        branchTable.refresh();
    }

    public void showInformation(String information){
        super.showInformation(information);
    }

    public void setListener(ViewListener listener) {
        this.listener = listener;
    }

    public interface ViewListener{
        /**
         * View navigator to the main menu.
         */
        void goBack();
        /**
         * Delete project branch
         */
        void onDeleteBranch(Branch branch);

        /**
         * Add branch to project
         */
        void onAddBranch(String branchName);

        /**
         * Change the current branch
         * @param branch the new current branch
         */
        void onCheckoutBranch(Branch branch);

        /**
         * Return the current branch
         * @return the current branch
         */
        Branch getCurrentBranch();

        void onMergeBranch(Branch branch);
    }
}