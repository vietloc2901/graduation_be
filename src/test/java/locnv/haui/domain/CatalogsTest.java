package locnv.haui.domain;

import static org.assertj.core.api.Assertions.assertThat;

import locnv.haui.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CatalogsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Catalogs.class);
        Catalogs catalogs1 = new Catalogs();
        catalogs1.setId(1L);
        Catalogs catalogs2 = new Catalogs();
        catalogs2.setId(catalogs1.getId());
        assertThat(catalogs1).isEqualTo(catalogs2);
        catalogs2.setId(2L);
        assertThat(catalogs1).isNotEqualTo(catalogs2);
        catalogs1.setId(null);
        assertThat(catalogs1).isNotEqualTo(catalogs2);
    }
}
