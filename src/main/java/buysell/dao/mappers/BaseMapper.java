package buysell.dao.mappers;

import java.util.List;
import org.mapstruct.*;

@MapperConfig(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueIterableMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT
)
public interface BaseMapper<E, D> {

    D toDto(E e);

    E toEntity(D d);

    List<D> toDtos(Iterable<E> list);
}
