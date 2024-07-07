package obi.com.grademaster.mapper;

import obi.com.grademaster.DTO.StudentDto;
import obi.com.grademaster.entity.Student;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.FIELD)
public abstract class StudentMapper {

    @Mapping(target = "scores", ignore = true)
    public abstract Student mapToStudent(StudentDto studentDto);
    public abstract StudentDto mapToStudentDto(Student student);

}
