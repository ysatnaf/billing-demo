import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import ProductPlan from './product-plan';
import ProductPlanDetail from './product-plan-detail';
import ProductPlanUpdate from './product-plan-update';
import ProductPlanDeleteDialog from './product-plan-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={ProductPlanUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={ProductPlanUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={ProductPlanDetail} />
      <ErrorBoundaryRoute path={match.url} component={ProductPlan} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={ProductPlanDeleteDialog} />
  </>
);

export default Routes;
