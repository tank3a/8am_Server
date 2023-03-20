package uos.capstone.dms.domain.pet;

import lombok.*;

@Getter
@NoArgsConstructor
public class PetImageDTO {

    private Long id;
    private String uuid;
    private String fileName;
    private String fileUrl;
    private String memberId;
    private String petId;

    @Builder
    public PetImageDTO(Long id, String uuid, String fileName, String fileUrl, String memberId, String petId) {
        this.id = id;
        this.uuid = uuid;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.memberId = memberId;
        this.petId = petId;
    }
}
