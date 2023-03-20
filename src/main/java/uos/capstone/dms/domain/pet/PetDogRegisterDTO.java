package uos.capstone.dms.domain.pet;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class PetDogRegisterDTO {

    private String name;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate birth;
    private int gender;
    private long breedId;
    private double weight;
    private MultipartFile petDogImage;

    public String generatePetId() {
        LocalDateTime time = LocalDateTime.now();
        return time.toString().concat(this.name);
    }
}
