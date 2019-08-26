import axios from 'axios';
import { ICrudSearchAction, ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IProductPlan, defaultValue } from 'app/shared/model/product-plan.model';

export const ACTION_TYPES = {
  SEARCH_PRODUCTPLANS: 'productPlan/SEARCH_PRODUCTPLANS',
  FETCH_PRODUCTPLAN_LIST: 'productPlan/FETCH_PRODUCTPLAN_LIST',
  FETCH_PRODUCTPLAN: 'productPlan/FETCH_PRODUCTPLAN',
  CREATE_PRODUCTPLAN: 'productPlan/CREATE_PRODUCTPLAN',
  UPDATE_PRODUCTPLAN: 'productPlan/UPDATE_PRODUCTPLAN',
  DELETE_PRODUCTPLAN: 'productPlan/DELETE_PRODUCTPLAN',
  RESET: 'productPlan/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IProductPlan>,
  entity: defaultValue,
  updating: false,
  updateSuccess: false
};

export type ProductPlanState = Readonly<typeof initialState>;

// Reducer

export default (state: ProductPlanState = initialState, action): ProductPlanState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_PRODUCTPLANS):
    case REQUEST(ACTION_TYPES.FETCH_PRODUCTPLAN_LIST):
    case REQUEST(ACTION_TYPES.FETCH_PRODUCTPLAN):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_PRODUCTPLAN):
    case REQUEST(ACTION_TYPES.UPDATE_PRODUCTPLAN):
    case REQUEST(ACTION_TYPES.DELETE_PRODUCTPLAN):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.SEARCH_PRODUCTPLANS):
    case FAILURE(ACTION_TYPES.FETCH_PRODUCTPLAN_LIST):
    case FAILURE(ACTION_TYPES.FETCH_PRODUCTPLAN):
    case FAILURE(ACTION_TYPES.CREATE_PRODUCTPLAN):
    case FAILURE(ACTION_TYPES.UPDATE_PRODUCTPLAN):
    case FAILURE(ACTION_TYPES.DELETE_PRODUCTPLAN):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.SEARCH_PRODUCTPLANS):
    case SUCCESS(ACTION_TYPES.FETCH_PRODUCTPLAN_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_PRODUCTPLAN):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_PRODUCTPLAN):
    case SUCCESS(ACTION_TYPES.UPDATE_PRODUCTPLAN):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_PRODUCTPLAN):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: {}
      };
    case ACTION_TYPES.RESET:
      return {
        ...initialState
      };
    default:
      return state;
  }
};

const apiUrl = 'api/product-plans';
const apiSearchUrl = 'api/_search/product-plans';

// Actions

export const getSearchEntities: ICrudSearchAction<IProductPlan> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_PRODUCTPLANS,
  payload: axios.get<IProductPlan>(`${apiSearchUrl}?query=${query}`)
});

export const getEntities: ICrudGetAllAction<IProductPlan> = (page, size, sort) => ({
  type: ACTION_TYPES.FETCH_PRODUCTPLAN_LIST,
  payload: axios.get<IProductPlan>(`${apiUrl}?cacheBuster=${new Date().getTime()}`)
});

export const getEntity: ICrudGetAction<IProductPlan> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_PRODUCTPLAN,
    payload: axios.get<IProductPlan>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IProductPlan> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_PRODUCTPLAN,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IProductPlan> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_PRODUCTPLAN,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const deleteEntity: ICrudDeleteAction<IProductPlan> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_PRODUCTPLAN,
    payload: axios.delete(requestUrl)
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
