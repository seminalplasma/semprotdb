import axios from 'axios';

import { type IDBConfig } from '@/shared/model/db-config.model';

const baseApiUrl = 'api/db-configs';

export default class DBConfigService {
  public find(id: number): Promise<IDBConfig> {
    return new Promise<IDBConfig>((resolve, reject) => {
      axios
        .get(`${baseApiUrl}/${id}`)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public retrieve(feedback = false): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      axios
        .get(feedback ? baseApiUrl.replace('db-configs', 'feedbacks') : `${baseApiUrl}`)
        .then(res => {
          resolve(res);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public delete(id: number): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      axios
        .delete(`${baseApiUrl}/${id}`)
        .then(res => {
          resolve(res);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public create(entity: IDBConfig, feedback = false): Promise<IDBConfig> {
    return new Promise<IDBConfig>((resolve, reject) => {
      axios
        .post(feedback ? baseApiUrl.replace('db-configs', 'feedbacks') : `${baseApiUrl}`, entity)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public update(entity: IDBConfig): Promise<IDBConfig> {
    return new Promise<IDBConfig>((resolve, reject) => {
      axios
        .put(`${baseApiUrl}/${entity.id}`, entity)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public partialUpdate(entity: IDBConfig): Promise<IDBConfig> {
    return new Promise<IDBConfig>((resolve, reject) => {
      axios
        .patch(`${baseApiUrl}/${entity.id}`, entity)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }
}
