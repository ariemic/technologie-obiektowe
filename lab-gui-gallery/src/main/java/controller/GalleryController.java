package controller;


import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import model.Gallery;
import model.Photo;
import org.pdfsam.rxjavafx.schedulers.JavaFxScheduler;
import util.PhotoDownloader;


public class GalleryController {

    @FXML
    private TextField imageNameField;

    @FXML
    private ImageView imageView;

    @FXML
    private ListView<Photo> imagesListView;

    @FXML
    private TextField searchTextField;

    private Gallery galleryModel;
    private PhotoDownloader photoDownloader;


    @FXML
    public void initialize() {
        // TODO additional FX controls initialization

        imagesListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Photo item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    ImageView photoIcon = new ImageView(item.getPhotoData());
                    photoIcon.setPreserveRatio(true);
                    photoIcon.setFitHeight(50);
                    setGraphic(photoIcon);
                }
            }
        });

        imagesListView.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            // observable to obserwowana wartość czyli selecteIndexProperty, czyli bierzący indeks elementu z Listview
            int newIndex = newValue.intValue();
            int oldIndex = oldValue.intValue();

            if(oldIndex > -1){
                Photo oldSelectedPhoto = galleryModel.getPhotos().get(oldIndex);
                imageNameField.textProperty().unbindBidirectional(oldSelectedPhoto.nameProperty());
            }
            if(newIndex >= 0){
                Photo newSelectedPhoto = galleryModel.getPhotos().get(newIndex);
                bindSelectedPhoto(newSelectedPhoto);
            }
        });
    }

    public void setModel(Gallery gallery) {
        this.galleryModel = gallery;
        imagesListView.getSelectionModel().select(0);
        ObservableList<Photo> photos = FXCollections.observableArrayList(gallery.getPhotos());
        imagesListView.setItems(photos);
    }

    private void bindSelectedPhoto(Photo selectedPhoto) {
        // TODO view <-> model bindings configuration
        imageView.imageProperty().bind(selectedPhoto.photoDataProperty());
        imageNameField.textProperty().bindBidirectional(selectedPhoto.nameProperty());

    }

    public void searchButtonClicked(ActionEvent event) {
        photoDownloader = new PhotoDownloader();
        galleryModel.clear();
        String query = searchTextField.getText().trim();
        if(query.isEmpty()){
            System.out.println("Searched text is empty.");
            return ;
        }
        Observable<Photo> photoObservable = photoDownloader.searchForPhotos(query);
        photoObservable
                .subscribeOn(Schedulers.io())
                .observeOn(JavaFxScheduler.platform())
                .subscribe(photo -> {
                    Platform.runLater(()-> galleryModel.addPhoto(photo));
                    System.out.println("Photo has been added. ");
                }, Throwable::printStackTrace);
    }
}

