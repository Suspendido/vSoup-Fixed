package kami.gg.souppvp.changelog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeLog {
    
    private String id;
    private String title;
    private List<String> content;
    private String author;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public ChangeLog(String id, String title, List<String> content, String author) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getFormattedDate() {
        return createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
    
    public String getFormattedUpdateDate() {
        return updatedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
