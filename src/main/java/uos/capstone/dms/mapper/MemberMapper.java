package uos.capstone.dms.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import uos.capstone.dms.domain.user.Member;
import uos.capstone.dms.domain.user.MemberJoinRequestDTO;
import uos.capstone.dms.domain.user.MemberResponseDTO;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    MemberMapper INSTANCE = Mappers.getMapper(MemberMapper.class);

    MemberResponseDTO toMemberResponseDTO(Member member);

    @Mapping(target = "role", constant = "ROLE_USER")
    @Mapping(target = "memberImage", ignore = true)
    @Mapping(target = "isSocial", ignore = true)
    Member joinRequestDTOToMember(MemberJoinRequestDTO memberJoinRequestDTO);
}
