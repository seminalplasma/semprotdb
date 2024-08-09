/* tslint:disable max-line-length */
import axios from 'axios';
import sinon from 'sinon';

import CargaService from './carga.service';
import { Carga } from '@/shared/model/carga.model';

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
  describe('Carga Service', () => {
    let service: CargaService;
    let elemDefault;

    beforeEach(() => {
      service = new CargaService();
      elemDefault = new Carga(
        123,
        'AAAAAAA',
        0,
        'image/png',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        false,
        'ARQUIVO',
        'TSV',
        'DADOS',
        0,
        'AAAAAAA',
      );
    });

    describe('Service methods', () => {
      it('should find an element', async () => {
        const returnedFromService = Object.assign({}, elemDefault);
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

      it('should create a Carga', async () => {
        const returnedFromService = Object.assign(
          {
            id: 123,
          },
          elemDefault,
        );
        const expected = Object.assign({}, returnedFromService);

        axiosStub.post.resolves({ data: returnedFromService });
        return service.create({}).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not create a Carga', async () => {
        axiosStub.post.rejects(error);

        return service
          .create({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should update a Carga', async () => {
        const returnedFromService = Object.assign(
          {
            status: 'BBBBBB',
            ordem: 1,
            planilha: 'BBBBBB',
            nome: 'BBBBBB',
            caminho: 'BBBBBB',
            validado: true,
            tipo: 'BBBBBB',
            formato: 'BBBBBB',
            destino: 'BBBBBB',
            linhas: 1,
            checksum: 'BBBBBB',
          },
          elemDefault,
        );

        const expected = Object.assign({}, returnedFromService);
        axiosStub.put.resolves({ data: returnedFromService });

        return service.update(expected).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not update a Carga', async () => {
        axiosStub.put.rejects(error);

        return service
          .update({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should partial update a Carga', async () => {
        const patchObject = Object.assign(
          {
            planilha: 'BBBBBB',
            formato: 'BBBBBB',
            destino: 'BBBBBB',
            linhas: 1,
            checksum: 'BBBBBB',
          },
          new Carga(),
        );
        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);
        axiosStub.patch.resolves({ data: returnedFromService });

        return service.partialUpdate(patchObject).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not partial update a Carga', async () => {
        axiosStub.patch.rejects(error);

        return service
          .partialUpdate({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should return a list of Carga', async () => {
        const returnedFromService = Object.assign(
          {
            status: 'BBBBBB',
            ordem: 1,
            planilha: 'BBBBBB',
            nome: 'BBBBBB',
            caminho: 'BBBBBB',
            validado: true,
            tipo: 'BBBBBB',
            formato: 'BBBBBB',
            destino: 'BBBBBB',
            linhas: 1,
            checksum: 'BBBBBB',
          },
          elemDefault,
        );
        const expected = Object.assign({}, returnedFromService);
        axiosStub.get.resolves([returnedFromService]);
        return service.retrieve({ sort: {}, page: 0, size: 10 }).then(res => {
          expect(res).toContainEqual(expected);
        });
      });

      it('should not return a list of Carga', async () => {
        axiosStub.get.rejects(error);

        return service
          .retrieve()
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should delete a Carga', async () => {
        axiosStub.delete.resolves({ ok: true });
        return service.delete(123).then(res => {
          expect(res.ok).toBeTruthy();
        });
      });

      it('should not delete a Carga', async () => {
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
