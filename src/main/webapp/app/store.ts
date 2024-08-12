import { useAccountStore as useStore } from '@/shared/config/store/account-store';
export type AccountStore = ReturnType<typeof useStore>;
export { useStore };

import { useTranslationStore } from '@/shared/config/store/translation-store';
export type TranslationStore = ReturnType<typeof useTranslationStore>;
export { useTranslationStore };

import { useVersaoStore } from '@/shared/config/store/versao-store';
export type VersaoStore = ReturnType<typeof useVersaoStore>;
export { useVersaoStore };
