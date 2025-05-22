import { createRouter as createVueRouter, createWebHistory } from 'vue-router';

const Home = () => import('@/core/home/home.vue');
const Error = () => import('@/core/error/error.vue');
import account from '@/router/account';
import admin from '@/router/admin';
import entities from '@/router/entities';
import pages from '@/router/pages';
import { Authority } from '@/shared/security/authority';

const Tabela = () => import('@/core/tabela/tabela.vue');
const Downloads = () => import('@/core/downloads/downloads.component.vue');
const Feedbacks = () => import('@/core/feedbacks/feedback.vue');

export const createRouter = () =>
  createVueRouter({
    history: createWebHistory('/'),
    routes: [
      {
        path: '/',
        name: 'Home',
        component: Home,
      },
      {
        path: '/tabela',
        name: 'Tabela',
        component: Tabela,
      },
      {
        path: '/downloads',
        name: 'Downloads',
        component: Downloads,
      },
      {
        path: '/feedbacks',
        name: 'Feedbacks',
        component: Feedbacks,
        meta: { authorities: [Authority.ADMIN] },
      },
      {
        path: '/forbidden',
        name: 'Forbidden',
        component: Error,
        meta: { error403: true },
      },
      {
        path: '/not-found',
        name: 'NotFound',
        component: Error,
        meta: { error404: true },
      },
      ...account,
      ...admin,
      entities,
      ...pages,
    ],
  });

const router = createRouter();

router.beforeResolve(async (to, from, next) => {
  if (!to.matched.length) {
    next({ path: '/not-found' });
    return;
  }
  next();
});

export default router;
