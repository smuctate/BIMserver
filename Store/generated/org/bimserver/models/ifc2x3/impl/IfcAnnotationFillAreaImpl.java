/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.bimserver.models.ifc2x3.impl;

import org.bimserver.models.ifc2x3.Ifc2x3Package;
import org.bimserver.models.ifc2x3.IfcAnnotationFillArea;
import org.bimserver.models.ifc2x3.IfcCurve;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Ifc Annotation Fill Area</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.bimserver.models.ifc2x3.impl.IfcAnnotationFillAreaImpl#getOuterBoundary <em>Outer Boundary</em>}</li>
 *   <li>{@link org.bimserver.models.ifc2x3.impl.IfcAnnotationFillAreaImpl#getInnerBoundaries <em>Inner Boundaries</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class IfcAnnotationFillAreaImpl extends IfcGeometricRepresentationItemImpl implements IfcAnnotationFillArea {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected IfcAnnotationFillAreaImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return Ifc2x3Package.eINSTANCE.getIfcAnnotationFillArea();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IfcCurve getOuterBoundary() {
		return (IfcCurve)eGet(Ifc2x3Package.eINSTANCE.getIfcAnnotationFillArea_OuterBoundary(), true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setOuterBoundary(IfcCurve newOuterBoundary) {
		eSet(Ifc2x3Package.eINSTANCE.getIfcAnnotationFillArea_OuterBoundary(), newOuterBoundary);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	public EList<IfcCurve> getInnerBoundaries() {
		return (EList<IfcCurve>)eGet(Ifc2x3Package.eINSTANCE.getIfcAnnotationFillArea_InnerBoundaries(), true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetInnerBoundaries() {
		eUnset(Ifc2x3Package.eINSTANCE.getIfcAnnotationFillArea_InnerBoundaries());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetInnerBoundaries() {
		return eIsSet(Ifc2x3Package.eINSTANCE.getIfcAnnotationFillArea_InnerBoundaries());
	}

} //IfcAnnotationFillAreaImpl