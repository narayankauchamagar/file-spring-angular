package np.com.thapanarayan.fp.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TokenDTO {
    private String token;
    private boolean check;
    private String file;
}
