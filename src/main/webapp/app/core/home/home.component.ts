import { type ComputedRef, defineComponent, inject, ref, type Ref, onMounted, computed, onBeforeUnmount } from 'vue';
import { useI18n } from 'vue-i18n';
import OrganismoService from '@/entities/organismo/organismo.service';
import type { IOrganismo } from '@/shared/model/organismo.model';
import type { IVersao } from '@/shared/model/versao.model';
import ovelha from '../../../content/images/Ovelha.svg';
import cabra from '../../../content/images/Cabra.svg';
import touro from '../../../content/images/Touro.svg';
import cavalo from '../../../content/images/Cavalo.svg';
import porco from '../../../content/images/Porco.svg';
import cachorro from '../../../content/images/Cachorro.svg';
import burro from '../../../content/images/Burro.svg';
import bufalo from '../../../content/images/Bufalo.svg';

const ORGANISMO_ICON_MAPPER: Record<string, string> = {
  'Ovis aries': ovelha,
  'Capra hircus': cabra,
  'Bos taurus': touro,
  'Equus caballus': cavalo,
  'Sus scrofa': porco,
  'Canis lupus': cachorro,
  'Equus asinus': burro,
  'Bubalus bubalis': bufalo,
};

export default defineComponent({
  compatConfig: { MODE: 3 },
  setup() {
    const versao = inject<ComputedRef<IVersao>>('versao');
    const organismoService = inject('organismoService', () => new OrganismoService());
    const organismos: Ref<IOrganismo[]> = ref([]);

    const versaoImagemSrc = computed(() => {
      if (versao?.value && versao.value.imagem) {
        return `data:${versao.value.imagemContentType};base64,${versao.value.imagem}`;
      }
      return '/content/images/semprot-logo2.svg'; // Default or placeholder image
    });

    // Refs for content sections
    const section1 = ref<HTMLElement | null>(null);
    const section2 = ref<HTMLElement | null>(null);
    const section3 = ref<HTMLElement | null>(null);
    const section4 = ref<HTMLElement | null>(null);

    let observer: IntersectionObserver | null = null;

    onMounted(async () => {
      try {
        const res = await organismoService().retrieve();
        organismos.value = res.data.map(org => ({
          ...org,
          // Use apelido first, then nome, then an empty string for the lookup.
          // Provide a fallback icon if no mapping is found.
          silhueta: ORGANISMO_ICON_MAPPER[org.apelido || org.nome || ''] || '/content/images/semprot-logo2.svg',
        }));
      } catch (error) {
        console.error('Failed to load organismos:', error);
      }

      const sections = [section1.value, section2.value, section3.value, section4.value].filter(Boolean) as HTMLElement[];

      if (sections.length > 0) {
        const observerOptions = {
          root: null, // relative to document viewport
          rootMargin: '0px',
          threshold: 0.1, // 10% of the item is visible
        };

        observer = new IntersectionObserver((entries, obs) => {
          entries.forEach(entry => {
            if (entry.isIntersecting) {
              entry.target.classList.add('is-visible');
              obs.unobserve(entry.target); // Stop observing once visible
            }
          });
        }, observerOptions);

        sections.forEach(section => {
          observer?.observe(section);
        });
      }
    });

    onBeforeUnmount(() => {
      if (observer) {
        observer.disconnect();
      }
    });

    const getOrganismStyle = (index: number, total: number, radius: number, itemSize: number) => {
      if (total === 0) return {};
      const angle = (index / total) * 2 * Math.PI - Math.PI / 2; // Start from top

      const x = radius * Math.cos(angle);
      const y = radius * Math.sin(angle);

      // JS now only calculates positioning. Transform is handled in CSS.
      const style = {
        left: `calc(50% + ${x}px)`,
        top: `calc(50% + ${y}px)`,
      };
      return style;
    };

    return {
      t$: useI18n().t,
      organismos,
      versao,
      versaoImagemSrc,
      getOrganismStyle,
      // Expose section refs to the template
      section1,
      section2,
      section3,
      section4,
    };
  },
});
