package np.com.thapanarayan.fp.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileResponse {

    private String fileName;
    private String fileUrl;
    private String message;

}
