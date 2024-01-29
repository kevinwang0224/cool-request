package com.cool.request.view.page;

import com.cool.request.component.http.net.FormDataInfo;
import com.cool.request.view.page.cell.*;
import com.cool.request.view.table.TableCellAction;
import com.intellij.openapi.project.Project;
import com.intellij.ui.table.JBTable;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;


public class FormDataRequestBodyPage extends BaseTablePanelWithToolbarPanelImpl {

    @Override
    protected Object[] getTableHeader() {
        return new Object[]{"", "Key", "Value", "Type", ""};
    }

    @Override
    protected Object[] getNewNullRowData() {
        return new Object[]{true, "", "", "text", ""};
    }

    @Override
    protected List<Integer> getSelectRow() {
        List<Integer> result = new ArrayList<>();
        foreachTable((objects, row) -> {
            if (Boolean.valueOf(objects.get(0).toString())) {
                result.add(row);
            }
        });
        return result;
    }

    @Override
    protected void initDefaultTableModel(JBTable jTable, DefaultTableModel defaultTableModel) {
        jTable.getColumnModel().getColumn(0).setMaxWidth(30);
        jTable.getColumnModel().getColumn(4).setMaxWidth(80);

        jTable.getColumnModel().getColumn(0).setCellRenderer(jTable.getDefaultRenderer(Boolean.class));
        jTable.getColumnModel().getColumn(0).setCellEditor(jTable.getDefaultEditor(Boolean.class));

        jTable.getColumnModel().getColumn(1).setCellEditor(new DefaultJTextCellEditable(getProject()));
        jTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultJTextCellRenderer());


        jTable.getColumnModel().getColumn(2).setCellRenderer(new FormDataRequestBodyValueRenderer());
        jTable.getColumnModel().getColumn(2).setCellEditor(new FormDataRequestBodyValueCellEditor(jTable, getProject()));

        jTable.getColumnModel().getColumn(3).setCellRenderer(new FormDataRequestBodyComboBoxRenderer(jTable));
        jTable.getColumnModel().getColumn(3).setCellEditor(new FormDataRequestBodyComboBoxEditor(jTable));

        jTable.getColumnModel().getColumn(4).setCellEditor(new TableCellAction.TableDeleteButtonCellEditor(this::deleteActionPerformed));
        jTable.getColumnModel().getColumn(4).setCellRenderer(new TableCellAction.TableDeleteButtonRenderer());

    }

    public FormDataRequestBodyPage(Project project) {
        super(project);

    }

    public void setFormData(List<FormDataInfo> value, boolean addNewLine) {
        if (value == null) value = new ArrayList<>();
        if (addNewLine) {
            value = new ArrayList<>(value);
            value.addAll(value);
            value.add(new FormDataInfo("", "", "text"));

        }
        removeAllRow();
        for (FormDataInfo formDataInfo : value) {
            addNewRow(new Object[]{true, formDataInfo.getName(), formDataInfo.getValue(), formDataInfo.getType(), ""});
        }
    }

    public void setFormData(List<FormDataInfo> value) {
        setFormData(value, false);
    }

    public List<FormDataInfo> getFormData() {
        List<FormDataInfo> result = new ArrayList<>();
        foreachTable((objects, integer) -> {
            if (!Boolean.valueOf(objects.get(0).toString())) return;
            String key = objects.get(1).toString().toString();
            if ("".equalsIgnoreCase(key)) return;
            result.add(new FormDataInfo(key,
                    objects.get(2).toString().toString(),
                    objects.get(3).toString().toString()));
        });
        return result;
    }


}