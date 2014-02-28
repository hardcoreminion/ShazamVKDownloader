package tk.zabozhanov.SharamVKSharing.response;

/**
 * Created by Denis Zabozhanov on 28/02/14.
 */
public class AudioItem {
    public int id;
    public int ownerId;
    public String artist;
    public String title;

    public AudioItem(int id, int ownerId, String artist, String title) {
        this.id = id;
        this.ownerId = ownerId;
        this.artist = artist;
        this.title = title;
    }
}
