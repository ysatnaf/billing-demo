import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { ICrudGetAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './product-plan.reducer';
import { IProductPlan } from 'app/shared/model/product-plan.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IProductPlanDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class ProductPlanDetail extends React.Component<IProductPlanDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { productPlanEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            ProductPlan [<b>{productPlanEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="code">Code</span>
            </dt>
            <dd>{productPlanEntity.code}</dd>
            <dt>
              <span id="name">Name</span>
            </dt>
            <dd>{productPlanEntity.name}</dd>
            <dt>Product</dt>
            <dd>{productPlanEntity.product ? productPlanEntity.product.id : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/product-plan" replace color="info">
            <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/product-plan/${productPlanEntity.id}/edit`} replace color="primary">
            <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
          </Button>
        </Col>
      </Row>
    );
  }
}

const mapStateToProps = ({ productPlan }: IRootState) => ({
  productPlanEntity: productPlan.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ProductPlanDetail);
