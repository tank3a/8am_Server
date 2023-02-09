package uos.capstone.dms.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import uos.capstone.dms.domain.user.Member;
import uos.capstone.dms.domain.user.MemberRequestDTO;
import uos.capstone.dms.domain.user.MemberResponseDTO;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    MemberMapper INSTANCE = Mappers.getMapper(MemberMapper.class);

    MemberResponseDTO memberToMemberResponseDTO(Member member);

    Member MemberRequestDTOToMember(MemberRequestDTO memberRequestDTO);
}
