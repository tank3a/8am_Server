package uos.capstone.dms.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import uos.capstone.dms.domain.user.Member;
import uos.capstone.dms.domain.user.MemberRequestDTO;
import uos.capstone.dms.domain.user.MemberDTO;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    MemberMapper INSTANCE = Mappers.getMapper(MemberMapper.class);

    MemberDTO memberToMemberDTO(Member member);

    Member memberDTOToMember(MemberDTO memberDTO);

    Member memberRequestDTOToMember(MemberRequestDTO memberRequestDTO);
}
