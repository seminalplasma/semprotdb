/* tslint:disable max-line-length */
import axios from 'axios';
import sinon from 'sinon';
import dayjs from 'dayjs';

import VersaoService from './versao.service';
import { DATE_TIME_FORMAT } from '@/shared/composables/date-format';
import { Versao } from '@/shared/model/versao.model';

const error = {
  response: {
    status: null,
    data: {
      type: null,
    },
  },
};

const axiosStub = {
  get: sinon.stub(axios, 'get'),
  post: sinon.stub(axios, 'post'),
  put: sinon.stub(axios, 'put'),
  patch: sinon.stub(axios, 'patch'),
  delete: sinon.stub(axios, 'delete'),
};

describe('Service Tests', () => {
  describe('Versao Service', () => {
    let service: VersaoService;
    let elemDefault;
    let currentDate: Date;

    beforeEach(() => {
      service = new VersaoService();
      currentDate = new Date();
      elemDefault = new Versao(
        123,
        'AAAAAAA',
        'AAAAAAA',
        currentDate,
        'AAAAAAA',
        'CRIADO',
        0,
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'image/png',
        'AAAAAAA',
      );
    });

    describe('Service methods', () => {
      it('should find an element', async () => {
        const returnedFromService = Object.assign(
          {
            release: dayjs(currentDate).format(DATE_TIME_FORMAT),
          },
          elemDefault,
        );
        axiosStub.get.resolves({ data: returnedFromService });

        return service.find(123).then(res => {
          expect(res).toMatchObject(elemDefault);
        });
      });

      it('should not find an element', async () => {
        axiosStub.get.rejects(error);
        return service
          .find(123)
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should create a Versao', async () => {
        const returnedFromService = Object.assign(
          {
            id: 123,
            release: dayjs(currentDate).format(DATE_TIME_FORMAT),
          },
          elemDefault,
        );
        const expected = Object.assign(
          {
            release: currentDate,
          },
          returnedFromService,
        );

        axiosStub.post.resolves({ data: returnedFromService });
        return service.create({}).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not create a Versao', async () => {
        axiosStub.post.rejects(error);

        return service
          .create({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should update a Versao', async () => {
        const returnedFromService = Object.assign(
          {
            nome: 'BBBBBB',
            detalhes: 'BBBBBB',
            release: dayjs(currentDate).format(DATE_TIME_FORMAT),
            label: 'BBBBBB',
            status: 'BBBBBB',
            numero: 1,
            logo: 'BBBBBB',
            log: 'BBBBBB',
            texto: 'BBBBBB',
            imagem: 'BBBBBB',
          },
          elemDefault,
        );

        const expected = Object.assign(
          {
            release: currentDate,
          },
          returnedFromService,
        );
        axiosStub.put.resolves({ data: returnedFromService });

        return service.update(expected).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not update a Versao', async () => {
        axiosStub.put.rejects(error);

        return service
          .update({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should partial update a Versao', async () => {
        const patchObject = Object.assign(
          {
            detalhes: 'BBBBBB',
            release: dayjs(currentDate).format(DATE_TIME_FORMAT),
            logo: 'BBBBBB',
          },
          new Versao(),
        );
        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign(
          {
            release: currentDate,
          },
          returnedFromService,
        );
        axiosStub.patch.resolves({ data: returnedFromService });

        return service.partialUpdate(patchObject).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not partial update a Versao', async () => {
        axiosStub.patch.rejects(error);

        return service
          .partialUpdate({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should return a list of Versao', async () => {
        const returnedFromService = Object.assign(
          {
            nome: 'BBBBBB',
            detalhes: 'BBBBBB',
            release: dayjs(currentDate).format(DATE_TIME_FORMAT),
            label: 'BBBBBB',
            status: 'BBBBBB',
            numero: 1,
            logo: 'BBBBBB',
            log: 'BBBBBB',
            texto: 'BBBBBB',
            imagem: 'BBBBBB',
          },
          elemDefault,
        );
        const expected = Object.assign(
          {
            release: currentDate,
          },
          returnedFromService,
        );
        axiosStub.get.resolves([returnedFromService]);
        return service.retrieve({ sort: {}, page: 0, size: 10 }).then(res => {
          expect(res).toContainEqual(expected);
        });
      });

      it('should not return a list of Versao', async () => {
        axiosStub.get.rejects(error);

        return service
          .retrieve()
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should delete a Versao', async () => {
        axiosStub.delete.resolves({ ok: true });
        return service.delete(123).then(res => {
          expect(res.ok).toBeTruthy();
        });
      });

      it('should not delete a Versao', async () => {
        axiosStub.delete.rejects(error);

        return service
          .delete(123)
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });
    });
  });
});
