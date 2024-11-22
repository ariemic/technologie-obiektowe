package app;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;
import model.Photo;
import model.PhotoSize;
import util.PhotoDownloader;
import util.PhotoProcessor;
import util.PhotoSerializer;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PhotoCrawler {

    private static final Logger log = Logger.getLogger(PhotoCrawler.class.getName());

    private final PhotoDownloader photoDownloader;

    private final PhotoSerializer photoSerializer;

    private final PhotoProcessor photoProcessor;

    public PhotoCrawler() throws IOException {
        this.photoDownloader = new PhotoDownloader();
        this.photoSerializer = new PhotoSerializer("./photos");
        this.photoProcessor = new PhotoProcessor();
    }

    public void resetLibrary() throws IOException {
        photoSerializer.deleteLibraryContents();
    }

    public void downloadPhotoExamples() {
        try {
            Observable<Photo> downloadedExamples = photoDownloader.getPhotoExamples();
            downloadedExamples
                    .compose(this::processPhotos)
                    .subscribe(photoSerializer::savePhoto);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Downloading photo examples error", e);
        }
    }

    public void downloadPhotosForQuery(String query) throws IOException, InterruptedException {
        // TODO Implement me :(
        photoDownloader.searchForPhotos(query)
                .take(10)
                .compose(this::processPhotos)
                .subscribe(photoSerializer::savePhoto,
                        error -> log.log(Level.SEVERE, "Downloading photo examples error", error));

    }
// Czy sposób obsługi Observable po stronie PhotoCrawler różni się w obu przykładach?
// nie trzeba robić try catcha w downloadPhotosForQuery -> zaimplementowane w samej metodzie
//Jakie wzorce projektowe zostały tu wykorzystane? Iterator i Observer


    public void downloadPhotosForMultipleQueries(List<String> queries) throws IOException, InterruptedException {
        photoDownloader.searchForPhotos(queries)
                .take(10)
                .subscribe(photoSerializer::savePhoto,
                        error -> log.log(Level.SEVERE, "Downloading photo examples error", error));
    }

//    public void downloadPhotosForMultipleQueries(List<String> queries) throws IOException, InterruptedException {
//        Observable.fromIterable(queries)
//                .flatMap(query -> photoDownloader.searchForPhotos(query).take(5))
//                .compose(this::processPhotos)
//                .subscribe(photoSerializer::savePhoto,
//                        error -> log.log(Level.SEVERE, "Downloading photo examples error", error));
//    }

//    private Observable<Photo> processPhotos(Observable<Photo> photoObservable) {
//        return photoObservable
//                .filter(photoProcessor::isPhotoValid)
//                .map(photoProcessor::convertToMiniature);
//    }



    private Observable<Photo> processPhotos(Observable<Photo> photoObservable) {
        return photoObservable
                .filter(photoProcessor::isPhotoValid)
                .groupBy(PhotoSize::resolve)
                .flatMap(groupedObservable -> {
                    if (groupedObservable.getKey() == PhotoSize.MEDIUM) {
                        return groupedObservable.buffer(5, TimeUnit.SECONDS)
                                .flatMap(photos -> Observable.fromIterable(photos)
                                        .map(photoProcessor::convertToMiniature));
                    } else {
                        return groupedObservable
                                .observeOn(Schedulers.computation())
                                .map(photoProcessor::convertToMiniature);
                    }
                })
                .subscribeOn(Schedulers.io());
    }



}



