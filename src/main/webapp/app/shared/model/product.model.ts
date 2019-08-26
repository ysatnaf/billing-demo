import { IProductPlan } from 'app/shared/model/product-plan.model';

export interface IProduct {
  id?: number;
  code?: string;
  name?: string;
  productPlans?: IProductPlan[];
}

export const defaultValue: Readonly<IProduct> = {};
