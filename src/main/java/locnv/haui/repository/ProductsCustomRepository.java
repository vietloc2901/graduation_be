package locnv.haui.repository;

import locnv.haui.service.dto.ProductFullDataDTO;
import locnv.haui.service.dto.ProductsDTO;

import java.math.BigInteger;
import java.util.List;

public interface ProductsCustomRepository {
    List<ProductFullDataDTO> search(ProductsDTO productsDTO, int page, int pageSize);
    BigInteger totalRecord(ProductsDTO productsDTO);

    List<ProductFullDataDTO> searchForView(ProductsDTO productsDTO);

    List<ProductFullDataDTO> searchForViewProduct(ProductsDTO productsDTO, int page, int pageSize);
    BigInteger totalRecordSearchForView(ProductsDTO productsDTO);
}
