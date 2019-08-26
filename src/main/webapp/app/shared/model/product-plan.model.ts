import { IProduct } from 'app/shared/model/product.model';

export interface IProductPlan {
  id?: number;
  code?: string;
  name?: string;
  product?: IProduct;
}

export const defaultValue: Readonly<IProductPlan> = {};
